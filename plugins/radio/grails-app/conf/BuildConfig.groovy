grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.plugin.location.core = "../core"

grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
		// uncomment to disable ehcache
		// excludes 'ehcache'
	}
	log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	repositories {
		inherits true // Whether to inherit repository definitions from plugins
	}
	dependencies {
		// specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

		// runtime 'mysql:mysql-connector-java:5.1.5'
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
		test ":codenarc:0.17"
		test ":spock:0.6"
		test ":geb:$gebVersion"

		test ":build-test-data:2.0.2"
		test ':remote-control:1.2'

		build(":tomcat:$grailsVersion", ":release:1.0.0") {
			export = false
		}
	}
}

