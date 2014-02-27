// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']


// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
grails.views.pagination.max = 1000
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true

grails.plugin.databasemigration.updateOnStartFileNames = ['changelog.groovy']
// Allow disabling of migrations via system property
println "MIGRATIONS: System.properties.'db.migrations' = ${System.properties['db.migrations']}"
grails.plugin.databasemigration.updateOnStart = System.properties['db.migrations'] != 'false'

// set per-environment settings
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
    }
}

// log4j configuration
// first check for external config

grails.config.locations = []
grails.config.locations << "file:${frontlinesms2.ResourceUtils.resourcePath}/log4j.groovy"
log4j = {
	environments {
		production {
			def conf = frontlinesms2.ResourceUtils.resourcePath
			println "Logging conf dir: $conf"
			rollingFile name:"prod",
					file:"$conf/standard.log",
					maxFileSize:10240000,
					threshold:org.apache.log4j.Level.INFO
			rollingFile name:"prod-stacktrace",
					file:"$conf/stacktrace.log",
					maxFileSize:10240000,
					threshold:org.apache.log4j.Level.WARN
		}
		development { console name:'dev', threshold:org.apache.log4j.Level.INFO }
		test { console name:'test', threshold:org.apache.log4j.Level.ERROR }
	}

	root {
		all 'dev', 'test', 'prod', 'prod-stacktrace'
	}


	all 'org.codehaus.groovy.grails.web.servlet',  //  controllers
		'org.codehaus.groovy.grails.web.pages', //  GSP
		'org.codehaus.groovy.grails.web.sitemesh', //  layouts
		'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
		'org.codehaus.groovy.grails.web.mapping', // URL mapping
		'org.codehaus.groovy.grails.commons', // core / classloading
		'org.codehaus.groovy.grails.plugins', // plugins
		'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
		'org.springframework',
		'org.hibernate',
		'net.sf.ehcache.hibernate'
}


// Added by the JQuery Validation plugin:
jqueryValidation.packed = true
jqueryValidation.cdn = false  // false or "microsoft"
jqueryValidation.additionalMethods = false

frontlinesms.plugins=['frontlinesms-core']
frontlinesms.blockedNotificationList = []

mobileNumbers.international.warn = true
mobileNumbers.nonNumeric.warn = true

upload.maximum.size=15728640
