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
	def gebVersion = '0.7.2'

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
		mavenRepo "http://192.168.0.200:8081/artifactory/simple/super-repo/"
		grailsRepo "http://192.168.0.200:8081/artifactory/simple/super-repo/"
		mavenRepo 'http://dev.frontlinesms.com/m2repo/'
		mavenCentral()

		grailsCentral()

		// uncomment these to enable remote dependency resolution from public Maven repositories
		//mavenCentral()
		//mavenRepo "http://snapshots.repository.codehaus.org"
		//mavenRepo "http://repository.codehaus.org"
		//mavenRepo "http://download.java.net/maven/2/"
		//mavenRepo "http://repository.jboss.com/maven2/"
	}

	dependencies {
		// specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

		// runtime 'mysql:mysql-connector-java:5.1.16'
		def seleniumVersion = '2.30.0'
		def camel = {
			def camelVersion = "2.9.4"
			"org.apache.camel:camel-$it:$camelVersion"
		}

		// TEST
		test camel('test')
		test "org.codehaus.geb:geb-spock:$gebVersion"
		test "org.seleniumhq.selenium:selenium-support:$seleniumVersion"
		test "org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion"
		test "org.seleniumhq.selenium:selenium-remote-driver:$seleniumVersion"

		// TODO this should be included in compile for TEST and DEV scopes, and excluded for PRODUCTION
		compile 'net.frontlinesms.test:hayescommandset-test:0.0.4'

		// COMPILE
		//compile 'net.frontlinesms.core:smslib:1.1.4'
		compile('net.frontlinesms.core:camel-smslib:0.0.7') {
			//excludes 'smslib'
		}
		['mail', 'http', 'smpp'].each { compile camel(it) }
		compile 'net.frontlinesms.core:serial:1.0.1'
		compile 'net.frontlinesms.core:at-modem-detector:0.8'
		runtime 'org.rxtx:rxtx:2.1.7'
		runtime 'javax.comm:comm:2.0.3'
		compile('org.codehaus.groovy.modules.http-builder:http-builder:0.6') {
			excludes "commons-logging", "xml-apis", "groovy"
		}
	}

	plugins {
		compile ":hibernate:$grailsVersion"
		runtime ":database-migration:1.0"
		runtime ":jquery:1.7.1"
		runtime ':jquery-ui:1.8.15'
		compile ':resources:1.2'

		runtime ":export:1.1"
		runtime ":markdown:1.0.0.RC1"
		runtime ':routing:1.2.2-camel-2.9.4'
		runtime ":csv:0.3.1"
		compile ":quartz2:2.1.6.2"

		compile ':platform-core:1.0.RC3-frontlinesms'

		test ":code-coverage:1.2.5"
		test ":codenarc:0.18.1"
		test ":spock:0.6"
		test ":geb:$gebVersion"

		test ':build-test-data:2.0.2'
		compile ':remote-control:1.3'
		compile(':functional-test-development:0.9.3') {
			exclude 'hibernate'
		}

		// Uncomment these (or add new ones) to enable additional resources capabilities
		//runtime ":zipped-resources:1.0"
		//runtime ":cached-resources:1.0"
		//runtime ":yui-minify-resources:0.1.4"

		build(":tomcat:$grailsVersion") {
			export = false
		}
	}
}

coverage {
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
	}
}

