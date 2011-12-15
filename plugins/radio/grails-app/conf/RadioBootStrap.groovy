import grails.util.Environment
import frontlinesms2.radio.*

class RadioBootStrap extends CoreBootStrap {
	def grailsApplication
		
	def init = { servletContext ->
		switch(Environment.current) {
			case Environment.DEVELOPMENT:
				//DB Viewer
				//org.hsqldb.util.DatabaseManager.main()
				// do custom init for dev here
				dev_initRadioShows()
				break
		}
	}

	def destroy = {
	}
	
	private def dev_initRadioShows() {
		 def r = new RadioShow(name: "Health")
		 r.save(failOnError: true, flush: true)
	}

}
