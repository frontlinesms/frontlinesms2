grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

environments {
	test {
		grails.server.port.http=8081
	}
}

grails.project.dependency.resolution = {
	def gebVersion = '0.7.0'

	// inherit Grails' default dependencies
	inherits("global") {
		// uncomment to disable ehcache
		// excludes 'ehcache'
	}
	log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	checksums true // Whether to verify checksums on resolve

	repositories {
		inherits true // Whether to inherit repository definitions from plugins

		grailsHome()
		grailsPlugins()

		mavenLocal()

		grailsCentral()

		mavenRepo 'http://dev.frontlinesms.com/m2repo/'
		mavenCentral()

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
		def seleniumVersion = '2.21.0'
		def camel = {
			def camelVersion = "2.9.2"
			"org.apache.camel:camel-$it:$camelVersion"
		}

		// TEST
		test camel('test')
		test "org.codehaus.geb:geb-spock:$gebVersion"
		test "org.seleniumhq.selenium:selenium-support:$seleniumVersion"
		test "org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion"

		// TODO this should be included in compile for TEST and DEV scopes, and excluded for PRODUCTION
		compile 'net.frontlinesms.test:hayescommandset-test:0.0.4'

		// COMPILE
		compile 'net.frontlinesms.core:camel-smslib:0.0.5-SNAPSHOT'
		['mail', 'http'].each { compile camel(it) }
		compile 'net.frontlinesms.core:serial:1.0.1'
		compile 'net.frontlinesms.core:at-modem-detector:0.3'
		runtime 'org.rxtx:rxtx:2.1.7'
		runtime 'javax.comm:comm:2.0.3'
	}

	plugins {
		runtime ":hibernate:$grailsVersion"
		runtime ":jquery-validation:1.7.3"
		runtime ":database-migration:1.0"
		runtime ":jquery:1.7.1"
		runtime ':jquery-ui:1.8.15'
		runtime ":resources:1.1.6"

		runtime ":export:1.1"
		runtime ":markdown:1.0.0.RC1"
		runtime ":routing:1.2.0"
		runtime ":csv:0.3.1"
		runtime ":quartz2:0.2.3"

		test ":code-coverage:1.2.5"
		test ":codenarc:0.16.1"
		test ":spock:0.6"
		test ":geb:$gebVersion"

		test ":build-test-data:2.0.2"
		test ':remote-control:1.2'

		// Uncomment these (or add new ones) to enable additional resources capabilities
		//runtime ":zipped-resources:1.0"
		//runtime ":cached-resources:1.0"
		//runtime ":yui-minify-resources:0.1.4"

		build ":tomcat:$grailsVersion"
	}
}

