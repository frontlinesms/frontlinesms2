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
		 def show1 = new RadioShow(name: "Health")
		 (1..20).collect {
			if(it > 10)
			 	show1.addToMessages(new Fmessage(src: '+3245678', text: "health show message $it", date:new Date()-1, inbound: true).save(failOnError: true))
			else
				show1.addToMessages(new Fmessage(src: '+3245678', text: "health show message $it", date:new Date()-2, inbound: true).save(failOnError: true))
		 }
		 show1.save(failOnError: true, flush: true)
		 def show2 = new RadioShow(name: "Morning Show").save(flush:true)
	}
	
}
