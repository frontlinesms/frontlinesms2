import grails.util.Environment
import frontlinesms2.radio.*
import frontlinesms2.*


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
		 def messageA = new Fmessage(src: '+3245678', dst: '+123456789', text: "What is diabetes?").save(failOnError: true)
		 def messageB = new Fmessage(src: 'Jill', dst: '+254115533', text: "I love life").save(failOnError: true)
		 r.addToMessages(messageA)
		 r.addToMessages(messageB)
		 r.save(failOnError: true, flush: true)
	}
	
}
