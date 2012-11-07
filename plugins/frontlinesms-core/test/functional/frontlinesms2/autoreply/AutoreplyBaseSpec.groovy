package frontlinesms2.autoreply

import frontlinesms2.*

class AutoreplyBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestAutoreply(){
		def a = new Autoreply(name:"Fruits", autoreplyText:"Hello, this is an autoreply message")
			a.addToKeywords(new Keyword(value:"MANGO"))
			a.addToKeywords(new Keyword(value:"ORANGE"))
			a.save(flush:true, failOnError:true)
	}

	static createTestMessages(Autoreply a) {
		(0..90).each {
			def m = Fmessage.build(src:'Robert', text:"Test message $it", date:new Date()-it)
			a.addToMessages(m)
			a.save(failOnError:true, flush:true)
		}
	}

	static createTestActivities() {
		Announcement.build(name:"Sample Announcement", sentMessageText:"Message to send")
		Fmessage.build(src:'announce')
	}
}

