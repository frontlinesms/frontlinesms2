package frontlinesms2.autoforward

import frontlinesms2.*

class AutoforwardBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestAutoforward(){
		remote {
			def a = new Autoforward(name:"News")
			a.contacts = (1..10).collect {
				Contact.build(name:"generated-contact-$it", mobile:it)
			}
			a.addToKeywords(value:"BREAKING")
			a.addToKeywords(value:"ALERT")
			a.sentMessageText = 'Message is \${message_text}'
			a.save(flush:true, failOnError:true)
			null
		}
	}

	static createTestMessages(String aName) {
		remote {
			def autoforward = Autoforward.findByName(aName)
			(0..90).each {
				def m = Fmessage.build(src:'Nagila', text:"Sudden shock $it", date:new Date()-it)
				a.addToMessages(m)
			}
			a.save(failOnError:true, flush:true)
			null
		}
	}

	static createTestActivities() {
		remote {
			Announcement.build(name:"Sample Announcement", sentMessageText:"Message to send")
			Fmessage.build(src:'announce')
			null
		}
	}
}

