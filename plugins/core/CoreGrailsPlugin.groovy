class CoreGrailsPlugin {
    def version = "2.0-SNAPSHOT"
    def grailsVersion = "2.0.3 > *"
	def pluginExcludes = ["grails-app/views/error.gsp",
			"grails-app/conf/CoreBootStrap.groovy",
			"grails-app/conf/CoreUrlMappings.groovy"]
    def author = "FrontlineSMS team"
    def authorEmail = "dev@frontlinesms.com"
    def title = "FrontlineSMS Core"
    def description = ""
    def documentation = "https://github.com/frontlinesms/frontlinesms2"
}
