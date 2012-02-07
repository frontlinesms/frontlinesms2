package frontlinesms2.announcement

import frontlinesms2.*

class AnnouncementBaseSpec extends grails.plugin.geb.GebSpec {
	
	def createTestAnnouncements() {
		new Announcement(messages:[], name:'New Office', sentMessageText:"We are opening a new office in Kitali next week!").save(failOnError:true, flush:true)
		new Announcement(messages:[], name:'Office Party', sentMessageText:"Office Party on Friday!").save(failOnError:true, flush:true)
	}

	def createTestMessages() {
		[new Fmessage(src:'Max', text:'I will be late', date: new Date() - 4, starred: true),
				new Fmessage(src:'Jane', text:'Meeting at 10 am', date: new Date() - 3),
				new Fmessage(src:'Patrick', text:'Project has started', date: new Date() - 2),
				new Fmessage(src:'Zeuss', text:'Sewage blocked', date: new Date() - 1)].each() {
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
