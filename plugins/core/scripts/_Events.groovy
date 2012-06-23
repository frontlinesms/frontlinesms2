import groovy.sql.Sql
import grails.util.Environment

eventDefaultStart = {
	createUnitTest = { Map args = [:] ->
		createSpec('unit', args)
	}
	createIntegrationTest = { Map args = [:] ->
		createSpec('integration', args)
	}
	createSpec = { String type, Map args ->
		def superClass
		// map test superclass to Spock equivalent
		switch (args["superClass"]) {
			case "Controller${type.capitalize()}TestCase":
				superClass = "ControllerSpec"
				break
			case "TagLibUnitTestCase":
				superClass = "TagLibSpec"
				break
		// TODO add a case for Camel Route integration test case
			default:
				superClass = "${type.capitalize()}Spec"
		}
		createArtifact name: args["name"], suffix: "${args['suffix']}Spec", type: "Spec", path: "test/${type}", superClass: superClass
	}

}

String currentTestPhase
eventTestPhaseStart = { phaseName ->
	currentTestPhase = phaseName
	junitReportStyleDir = "test/conf"
}

eventTestStart = { testName ->
	if (currentTestPhase == 'functional') {
		def sql = Sql.newInstance('jdbc:h2:mem:testDb', 'sa', '', 'org.h2.Driver')
		sql.execute "SET REFERENTIAL_INTEGRITY FALSE"
		sql.eachRow("SHOW TABLES") { table -> sql.execute('DELETE FROM ' + table.TABLE_NAME) } 
		sql.execute "SET REFERENTIAL_INTEGRITY TRUE"
	}

	if(currentTestPhase == 'unit') {
		println 'Adding standard FrontlineSMS metaclass modifications...'
		frontlinesms2.MetaClassModifiers.addAll()
	}
}

eventTestPhaseEnd = { phaseName ->
	if (phaseName == 'functional' && new File('target/test-reports/geb').exists()) {
		def report = new File('target/test-reports/html/screenshots.html')
		new File('target/test-reports/geb').eachFileRecurse { f ->
			if(!f.name.endsWith('.png')) return
			report.append '<img height="120" src="..' +
			f.path.substring('target/test-reports'.size()) + '"/>\n'
		}
	}
}

def getApplicationProperty(key) {
	def MATCHER = key.replace('.', /\./) + /=(.*)/
	def match
	new File('application.properties').eachLine {
		if(it ==~ MATCHER) match = (it =~ MATCHER)[0][1]
	}
	return match
}

eventCompileStart = { kind ->
	if(Environment.current == Environment.PRODUCTION) {
		// Check we have no snapshot dependencies
		if(new File('grails-app/conf/BuildConfig.groovy').text.contains('SNAPSHOT')) {
			println '##################################'
			println '# YOU HAVE SNAPSHOT DEPENDENCIES #'
			println '##################################'

			def appVersion = getApplicationProperty('app.version')
			if(appVersion.contains('SNAPSHOT')) {
				println '# Press ENTER to continue...'
				System.in.withReader { it.readLine() }
			} else {
				println '# You cannot include SNAPSHOT dependencies in a release.'
				println '# Build terminating.'
				System.exit(1);
			}
		}
	}
}

eventCompileEnd = {
	// Copy i18n properties files to web-app so they are available for i18nService in dev mode
	def folderMap = [
		"grails-app/i18n":"web-app/WEB-INF/grails-app/i18n"
	]
	folderMap.each {oldLocation, newLocation ->
		ant.copy(toDir: newLocation) {
			fileset(dir:oldLocation)
		}	
	}

	// Rewrite the message properties files as JSON so they are available in Javascript in the app
	def appName = getApplicationProperty('app.name')
println "appName: $appName"
	new File('web-app/i18n').mkdir()
	new File('grails-app/i18n/').listFiles().each { f ->
		def builder = new groovy.json.JsonBuilder()
		def props = new Properties()
		f.withInputStream { stream -> props.load(stream); }

		def m = [:]
		props.each { k, v -> m[k] = v }
		builder(m)

		jsFilename = appName + '_' + f.name - '.properties' + '.js'
		new File("web-app/i18n/$jsFilename").text =
				"var i18nStrings = i18nStrings || {}; i18nStrings[\"$appName\"] = $builder;"
	}
}

