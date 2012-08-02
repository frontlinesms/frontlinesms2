import org.apache.log4j.ConsoleAppender
import org.apache.log4j.RollingFileAppender
// configuration for plugin testing - will not be included in the plugin zip
grails.project.groupId = "frontlinesms2.radio" // change this to alter the default package name and Maven publishing destination
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

//Enable automatic database migrations
grails.plugin.databasemigration.updateOnStart = true
grails.plugin.databasemigration.updateOnStartFileNames = ['changelog.groovy']

// pagination
grails.views.pagination.max = 50

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    warn 'org.codehaus.groovy.grails.web.servlet',  //  controllers
  environments {
    def layout = pattern(conversionPattern:'%d %-5p [%c{2}] %m%n')
    production {
      def conf = frontlinesms2.ResourceUtils.resourcePath
      appender new RollingFileAppender(name:"prod",
          layout:layout, file:"$conf/standard.log",
          threshold:org.apache.log4j.Level.INFO)
      appender new RollingFileAppender(name:"prod-stacktrace",
          layout:layout, file:"$conf/stacktrace.log",
          threshold:org.apache.log4j.Level.ERROR)
      info 'prod-stacktrace'
      error 'prod'
    }
    development {
      appender new ConsoleAppender(name:'console-logger',
          layout:layout,
          threshold:org.apache.log4j.Level.INFO)
      info 'console-logger'
    }
  }

  warn 'org.codehaus.groovy.grails.web.servlet',  //  controllers
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

frontlinesms.plugins=['core', 'radio']

