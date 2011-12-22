import grails.util.Environment
import frontlinesms2.radio.*
import frontlinesms2.*

import java.util.Date

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
		 (1..20).collect {
			if(it > 10)
			 	r.addToMessages(new Fmessage(src: '+3245678', dst: '+123456789', text: "health show message $it", dateReceived:new Date()-1).save(failOnError: true))
			else
				r.addToMessages(new Fmessage(src: '+3245678', dst: '+123456789', text: "health show message $it", dateReceived:new Date()-2).save(failOnError: true))
		 }
		 r.save(failOnError: true, flush: true)
	}
	
}
