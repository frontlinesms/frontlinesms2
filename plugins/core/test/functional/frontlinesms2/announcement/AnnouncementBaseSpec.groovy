package frontlinesms2.announcement

import frontlinesms2.*

class AnnouncementBaseSpec extends grails.plugin.geb.GebSpec {
	
	def createTestAnnouncements() {
		new Announcement(messages:[], name:'New Office', sentMessageText:"We are opening a new office in Kitali next week!").save(failOnError:true, flush:true)
		new Announcement(messages:[], name:'Office Party', sentMessageText:"Office Party on Friday!").save(failOnError:true, flush:true)
	}

	def createTestMessages() {
		[new Fmessage(src:'Max', dst:'+254987654', text:'I will be late', dateReceived: new Date() - 4, starred: true),
				new Fmessage(src:'Jane', dst:'+2541234567', text:'Meeting at 10 am', dateReceived: new Date() - 3),
				new Fmessage(src:'Patrick', dst:'+254112233', text:'Project has started', dateReceived: new Date() - 2),
				new Fmessage(src:'Zeuss', dst:'+234234', text:'Sewage blocked', dateReceived: new Date() - 1)].each() {
			it.inbound = true
			it.save(failOnError:true, flush:true)
		}
		[Announcement.findByName('New Office').addToMessages(Fmessage.findBySrc('Max')),
				Announcement.findByName('New Office').addToMessages(Fmessage.findBySrc('Jane')),
				Announcement.findByName('Office Party').addToMessages(Fmessage.findBySrc('Zeuss')),
				Announcement.findByName('Office Party').addToMessages(Fmessage.findBySrc('Patrick'))].each() {
			it.save(failOnError:true, flush:true)
		}
	}
}
