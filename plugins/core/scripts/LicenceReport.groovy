import au.com.bytecode.opencsv.*

import javax.xml.parsers.*

import groovy.xml.NamespaceBuilder

import org.xml.sax.*
import org.xml.sax.helpers.DefaultHandler
import org.codehaus.groovy.grails.resolve.IvyDependencyManager


includeTargets << grailsScript("_GrailsSettings")

// Thanks to grails-csv-plugin for basic approach - unfortunately couldn't use it directly
File.metaClass.eachCsvLine = { Closure c ->
	CSVReader reader = new CSVReader(delegate.newReader())
	try {
		String[] tokens
		while(tokens=reader.readNext()) c.call(tokens)
	} finally { reader.close() }
}

target(licenceReport:"Produces a report for the current Grails application") {
	depends('xmlDependencyReport')

	def mapOfMaps
	mapOfMaps = { [:].withDefault(mapOfMaps) }
	def licenceDb = mapOfMaps()
	// read licences CSV
	new File('licences.csv').eachCsvLine { tokens ->
		if(tokens.length >= 5) {
			def artifact = [:]
			['groupId', 'artifactId', 'version', 'licence', 'url'].eachWithIndex { o, i ->
				def token = tokens[i].trim()
				switch(o) {
					case 'groupId': case 'artifactId':
						token = token.toLowerCase(); break
					case 'version':
						token = LicenceUtils.sanitiseVersionNumber(token); break
				}
				artifact[o] = token
			}
			
			licenceDb."$artifact.groupId"."$artifact.artifactId"."$artifact.version" += [name:artifact.licence, proof:artifact.url]
		}
	}
//	println "licenceDb: $licenceDb"

	def dependencyReportDirectory = "$projectTargetDir/dependency-report"
	def licenceReportDirectory = "$projectTargetDir/licence-report"
	ant.delete(dir:licenceReportDirectory, failonerror:false)
	ant.mkdir(dir:licenceReportDirectory)

	def reader = SAXParserFactory.newInstance().newSAXParser().XMLReader
	def conf = args.trim() ?: 'build, compile, provided, runtime, test'
	conf.split(',')*.trim().each {
		println "Generating licence report for: '$it'"

		def outputReportFile = new File(licenceReportDirectory, "licences-${it}.csv")
		def writer = outputReportFile.newWriter()
		try {
			def reportHandler = new DependencyReportXmlHandler(writer)
			reportHandler.licenceDb = licenceDb
			reader.contentHandler = reportHandler

			def f = new File(dependencyReportDirectory, "org.grails.internal-$grailsAppName-${it}.xml")
			reader.parse(new InputSource(f.newInputStream()))

			def reportWriter = new File(licenceReportDirectory, "licence-report-${it}.txt").newWriter()
			def report = { reportWriter << "$it\n" }
			report "Licence report for $grailsAppName-$it:"
			report "	Dependency licences"
			report "		count	| licence"
			reportHandler.licenceCount.each { licence, count ->
				report "		$count 	| $licence"
			}
			report "	Licence unknown:"
			reportHandler.unlicensed.each { report "		$it" }
			reportWriter.close()
		} finally { writer.close() }
	}
}

/**
 * This target modified from grails core and licensed under Apache 2.0 licence
 * @author Graeme Rocher
 * @author Alex Anderson
 */
target(xmlDependencyReport:"Produces an XML dependency report for the current Grails application") {
	// create ivy namespace
	ivy = NamespaceBuilder.newInstance(ant, 'antlib:org.apache.ivy.ant')

	String targetDir = "$projectTargetDir/dependency-report"
	ant.delete(dir:targetDir, failonerror:false)
	ant.mkdir(dir:targetDir)

	println "Obtaining dependency data..."
	IvyDependencyManager dependencyManager = grailsSettings.dependencyManager
	for (conf in IvyDependencyManager.ALL_CONFIGURATIONS) {
		dependencyManager.resolveDependencies(conf)
	}

	def conf = args.trim()?: 'build, compile, provided, runtime, test'
	ivy.report(organisation:'org.grails.internal', module:grailsAppName, todir:targetDir, conf:conf, xml:true)

	println "Dependency report output to [$targetDir]"
}


setDefaultTarget(licenceReport)

class LicenceUtils {
	static def sanitiseVersionNumber(String v) {
		if(v.endsWith('RELEASE')) v = v[0..-('RELEASE'.size()+1)]
		if(v.endsWith('.')) v = v[0..-2]
		v
	}
}


class DependencyReportXmlHandler extends org.xml.sax.helpers.DefaultHandler {
	def writer
	def licenceDb
	def licenceCount = [:].withDefault { 0 }
	def unlicensed = []

//> PARSING VARIABLES
	def module

	DependencyReportXmlHandler(Writer w) {
		writer = new CSVWriter(w)
	}

	void startElement(String ns, String localName, String name, Attributes a) {
		switch(name) {
			case 'module': module=[org:a.getValue('organisation'), name:a.getValue('name')]; break
			case 'license':
				module.licenceName = a.getValue('name')
				module.licenceUrl = a.getValue('url')
				module.licenceSource = 'ivy'
				break
			case 'revision': module.revision=LicenceUtils.sanitiseVersionNumber(a.getValue('name')); break
			case 'artifact':
				if(!module.licenceName && licenceDb) {
					def dbEntry = licenceDb."$module.org"."$module.name"."$module.revision"
//					println "Found $dbEntry when looking for licence for $module.name ($module.revision) in ${licenceDb[module.org]}"
					def dbName = dbEntry.name
					if(dbName) {
						module.licenceName = dbName
						module.licenceUrl = dbEntry.url
						module.licenceProof = 'licenceDb'
					} else {
						unlicensed << module
					}
				}
				++licenceCount[module.licenceName]

				if(writer) writer.writeNext(module.values() as String[])
				else {
					println "Found an artifact:"
					println "	org: $module.org"
					println "	name: $module.name"
					println "	version: $module.revision"
					println "	licence: $module.licenceName"
				}
				break;
		}
	}
}
