package frontlinesms2.autoforward

import frontlinesms2.*

class AutoforwardBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestAutoforward(){
		def a = new Autoforward(name:"NEWS")
			a.addToKeywords(new Keyword(value:"BREAKING"))
			a.addToKeywords(new Keyword(value:"ALERT"))
			a.save(flush:true, failOnError:true)
	}

	static createTestMessages(Autoforward a) {
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

