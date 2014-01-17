package frontlinesms2.autoreply

import frontlinesms2.*

class AutoreplyBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestAutoreply() {
		remote {
			def a = new Autoreply(name:"Fruits", autoreplyText:"Hello, this is an autoreply message")
			a.addToKeywords(new Keyword(value:"MANGO"))
			a.addToKeywords(new Keyword(value:"ORANGE"))
			a.save(flush:true, failOnError:true)
			a.id
		}
	}

	static createTestMessages(String aName) {
		remote {
			def a = Autoreply.findByName(aName)
			(0..90).each {
				def m = TextMessage.build(src:'Robert', text:"Test message $it", date:new Date()-it)
				a.addToMessages(m)
			}
			a.save(failOnError:true, flush:true)
			null
		}
	}

	static createTestActivities() {
		remote {
			Announcement.build(name:"Sample Announcement", sentMessageText:"Message to send")
			TextMessage.build(src:'announce')
			null
		}
	}
}

