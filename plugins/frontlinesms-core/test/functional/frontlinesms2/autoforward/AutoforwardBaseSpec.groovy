package frontlinesms2.autoforward

import frontlinesms2.*

class AutoforwardBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestAutoforward(){
		def a = new Autoforward(name:"News",contacts:createContacts())
			a.addToKeywords(new Keyword(value:"BREAKING"))
			a.addToKeywords(new Keyword(value:"ALERT"))
			a.save(flush:true, failOnError:true)
	}

	static createContacts(){
		def contacts = []
		(0..100).each {
			contacts[it] = new Contact(name:"user + ${it}")
		}
		contacts
	}

	static createTestMessages(Autoforward a) {
		(0..90).each {
			def m = Fmessage.build(src:'Nagila', text:"Sudden shock $it", date:new Date()-it)
			a.addToMessages(m)
			a.save(failOnError:true, flush:true)
		}
	}

	static createTestActivities() {
		Announcement.build(name:"Sample Announcement", sentMessageText:"Message to send")
		Fmessage.build(src:'announce')
	}
}

