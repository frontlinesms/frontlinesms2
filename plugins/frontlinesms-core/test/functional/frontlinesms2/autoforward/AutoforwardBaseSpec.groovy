package frontlinesms2.autoforward

import frontlinesms2.*

class AutoforwardBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestAutoforward(){
		def a = new Autoforward(name:"News")
		a.contacts = createContacts()
		a.addToKeywords(value:"BREAKING")
		a.addToKeywords(value:"ALERT")
		a.sentMessageText = 'Message is \${message_text}'
		a.save(flush:true, failOnError:true)
	}

	static createContacts() {
		(1..10).collect {
			Contact.build(name:"generated-contact-$it", mobile:it)
		}
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

