grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = 'target'
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

environments {
	test {
		grails.server.port.http=8081
	}
}

grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
		// uncomment to disable ehcache
		// excludes 'ehcache'
	}
	log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	checksums true // Whether to verify checksums on resolve

	repositories {
		inherits true // Whether to inherit repository definitions from plugins

		grailsHome()
		grailsPlugins()

		mavenLocal()
		mavenCentral()
		mavenRepo 'http://dev.frontlinesms.com/m2repo/'

		grailsCentral()

		// uncomment these to enable remote dependency resolution from public Maven repositories
		//mavenCentral()
		//mavenRepo "http://snapshots.repository.codehaus.org"
		//mavenRepo "http://repository.codehaus.org"
		//mavenRepo "http://download.java.net/maven/2/"
		//mavenRepo "http://repository.jboss.com/maven2/"
	}

	dependencies {
		def camel = {
			def camelVersion = "2.9.4"
			"org.apache.camel:camel-$it:$camelVersion"
		}

		// TEST
		test camel('test')

		// TODO this should be included in compile for TEST and DEV scopes, and excluded for PRODUCTION
		compile 'net.frontlinesms.test:hayescommandset-test:0.0.4'

		// COMPILE
		//compile 'net.frontlinesms.core:smslib:1.1.4'
		compile('net.frontlinesms.core:camel-smslib:0.0.7') {
			//excludes 'smslib'
		}
		['mail', 'http', 'smpp'].each { compile camel(it) }
		compile 'net.frontlinesms.core:serial:1.0.1'
		compile 'net.frontlinesms.core:at-modem-detector:0.10'
		compile 'org.rxtx:rxtx:2.1.7'
		compile 'javax.comm:comm:2.0.3'
		compile('org.codehaus.groovy.modules.http-builder:http-builder:0.6') {
			excludes "commons-logging", "xml-apis", "groovy"
		}
		compile 'com.googlecode.libphonenumber:libphonenumber:4.3'
		compile 'com.googlecode.ez-vcard:ez-vcard:0.9.0'
		compile 'org.apache.httpcomponents:httpclient:4.3'
	}

	plugins {
		test ':frontlinesms-grails-test:0.18'

		compile ":hibernate:$grailsVersion"
		compile ":database-migration:1.0"
		compile ":jquery:1.7.1"
		compile ':jquery-ui:1.8.15'
		compile ':resources:1.2'

		compile ":export:1.1"
		compile ":markdown:1.0.0.RC1"
		compile ':routing:1.2.2-camel-2.9.4'
		compile ":csv:0.3.1"
		compile ":quartz2:2.1.6.2"

		compile ':platform-core:1.0.RC3-frontlinesms'

		compile ":flashier-messages:1.0", {
			excludes 'spock'
		}

		// Uncomment these (or add new ones) to enable additional resources capabilities
		//runtime ":zipped-resources:1.0"
		//runtime ":cached-resources:1.0"
		//runtime ":yui-minify-resources:0.1.4"

		build ":release:$grailsVersion", {
			excludes 'http-builder'
			export = false
		}
		build ":tomcat:$grailsVersion", {
			export = false
		}
		build ':bails:0.6'
		compile ':font-awesome-resources:3.2.1.3'

		// FIXES as per http://stackoverflow.com/questions/14581009/unknown-plugin-included-in-war-when-building
		test ':build-test-data:2.0.5', {
			export = false
		}
		test ':remote-control:1.4', {
			export = false
		}
		test ':geb:0.7.2', {
			export = false
		}
	}
}

coverage {
	enabledByDefault = false
	exclusions = ["**/*Spec"]
	xml = true
}

codenarc {
	reports = {
		xmlReport('xml') {
			outputFile = 'target/analysis-reports/codenarc.xml'
			title = 'CodeNarc Report'
		}
		htmlReport('html') {
			outputFile = 'target/analysis-reports/codenarc.html'
			title = 'CodeNarc Report'
		}
	}
	systemExitOnBuildException = false
	// NB these numbers should be LOWERED over time as code quality should be INCREASING
	maxPriority1Violations = 0
	maxPriority2Violations = 260
	maxPriority3Violations = 500

	properties = {
		GrailsPublicControllerMethod.enabled = false
		GrailsDomainHasToString.enabled = false
		GrailsDomainHasEquals.enabled = false
		ThrowRuntimeException.enabled = false
		CatchException.enabled = false
		MisorderedStaticImports.enabled = false
		EmptyMethod.doNotApplyToClassNames = '*Controller'
	}
}

