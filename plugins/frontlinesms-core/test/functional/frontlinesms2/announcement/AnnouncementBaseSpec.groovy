package frontlinesms2.announcement

import frontlinesms2.*

class AnnouncementBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestAnnouncements() {
		remote {
			Announcement.build(name:'New Office', sentMessageText:"We are opening a new office in Kitali next week!")
			Announcement.build(name:'Office Party', sentMessageText:"Office Party on Friday!")
			null
		}
	}

	def createTestMessages() {
		remote {
			TextMessage.build(src:'Max', text:'I will be late', date:new Date()-4, starred:true)
			TextMessage.build(src:'Jane', text:'Meeting at 10 am', date:new Date()-3)
			TextMessage.build(src:'Patrick', text:'Project has started', date:new Date()-2)
			TextMessage.build(src:'Zeuss', text:'Sewage blocked', date:new Date()-1)

			def newOffice = Announcement.findByName('New Office')
			['Max', 'Jane'].each { newOffice.addToMessages(TextMessage.findBySrc(it)) }
			newOffice.save(failOnError:true, flush:true)

			def officeParty = Announcement.findByName('Office Party')
			['Zeuss', 'Patrick'].each { officeParty.addToMessages(TextMessage.findBySrc(it)) }
			officeParty.save(failOnError:true, flush:true)
			null
		}
	}
}

