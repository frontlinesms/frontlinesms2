import frontlinesms2.radio.*
class RadioGrailsPlugin {
    // the plugin version
    def version = "0.a7"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [core:"0.b1-SNAPSHOT"]
    // resources that are excluded from plugin packaging
    // def pluginExcludes = ["grails-app/views/error.gsp"]

    // TODO Fill in these fields
    def author = "FrontlineSMS team"
    def authorEmail = "dev@frontlinesms.com"
    def title = "FrontlineSMS:Radio"
    def description = ""
	
	// URL to the plugin's documentation
    def documentation = "https://github.com/frontlinesms/frontlinesms2"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
	}

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
