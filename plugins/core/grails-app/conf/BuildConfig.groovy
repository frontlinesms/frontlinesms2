grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
		// uncomment to disable ehcache
		// excludes 'ehcache'
		excludes 'xml-apis'
	}
	log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	repositories {
		grailsHome()
		
		// uncomment the below to enable remote dependency resolution
		// from public Maven repositories
		mavenLocal()
		mavenCentral()
		//mavenRepo "http://snapshots.repository.codehaus.org"
		//mavenRepo "http://repository.codehaus.org"
		//mavenRepo "http://download.java.net/maven/2/"
		//mavenRepo "http://repository.jboss.com/maven2/"
		mavenRepo "http://dev.frontlinesms.com/m2repo/"
		mavenRepo "https://nexus.codehaus.org/content/repositories/snapshots/"
		
		grailsPlugins()
		grailsCentral()
	}
	
	dependencies {
		// specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

		// TEST
		def camel = { 
			def camelVersion = '2.5.0'
			"org.apache.camel:camel-$it:$camelVersion"
		}
		test camel('test')
		test 'org.mockito:mockito-all:1.8.5'
		def seleniumVersion = "2.21.0"
		def gebVersion = "0.6.1"
		def spockVersion = "0.5-groovy-1.7"
		test "org.seleniumhq.selenium:selenium-support:$seleniumVersion"
		test "org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion"
		test "org.seleniumhq.selenium:selenium-htmlunit-driver:$seleniumVersion"
		test "org.codehaus.geb:geb-spock:$gebVersion"
		test "org.spockframework:spock-core:$spockVersion"

		// TODO This should be included in compile for TEST and DEV scopes, and excluded for PRODUCTION
		compile 'net.frontlinesms.test:hayescommandset-test:0.0.4'

		// COMPILE
		compile 'net.frontlinesms.core:camel-smslib:0.0.4'
		['mail', 'http'].each { compile camel(it) }

		compile 'net.frontlinesms.core:serial:1.0.1'
		compile 'net.frontlinesms.core:at-modem-detector:0.2'
		runtime 'org.rxtx:rxtx:2.1.7'
		runtime 'javax.comm:comm:2.0.3'
	}
}

coverage {
	xml = true
	enabledByDefault = false
}

codenarc.reports = {
	MyXmlReport('xml') {
		outputFile = 'target/test-reports/CodeNarcReport.xml'
		title = 'FrontlineSMS2 CodeNarc Report (xml)'
	}
	
	MyHtmlReport('html') {
		outputFile = 'target/test-reports/CodeNarcReport.html'
		title = 'FrontlineSMS2 CodeNarc Report (html)'
	}
}
