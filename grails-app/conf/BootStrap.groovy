import grails.util.Environment
class BootStrap {
	def grailsApplication
	
	def init = { servletContext ->
		
		switch(Environment.current) {
			case Environment.TEST:
				test_initGeb()
				break
				
			case Environment.DEVELOPMENT:
				//DB Viewer
				//org.hsqldb.util.DatabaseManager.main()
				// do custom init for dev here
				if(System.properties['radio.plugin']) {
					grailsApplication.config.frontlinesms2.plugin = "radio"
				}
				break
		}
	}

	def destroy = {
	}
}
