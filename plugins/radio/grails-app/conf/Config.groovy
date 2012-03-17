// configuration for plugin testing - will not be included in the plugin zip
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
					  pdf: 'application/pdf',
					  all: '*/*',
					  json: ['application/json','text/json'],
					  form: 'application/x-www-form-urlencoded',
					  multipartForm: 'multipart/form-data'
					]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// jquery plugin
grails.views.javascript.library = "jquery"

//fronlinesms plugin
frontlinesms2.plugin = "radio"

// pagination
grails.views.pagination.max = 50
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
	
	production {
		appender new org.apache.log4j.DailyRollingFileAppender(name:"myLogger1", datePattern:"'.'yyyy-MM-dd", layout:pattern(conversionPattern:'%d %-5p [%c{2}] %m%n'), file:"${System.properties.'user.home'}/.frontlinesms2/standard.log");
		appender new org.apache.log4j.DailyRollingFileAppender(name:"myLogger1-Stacktrace", datePattern:"'.'yyyy-MM-dd", layout:pattern(conversionPattern:'%d %-5p [%c{2}] %m%n'), file:"${System.properties.'user.home'}/.frontlinesms2/stacktrace.log");
	}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
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

    warn   'org.mortbay.log'
    root {
    	info 'myLogger1'
    }
}
