package frontlinesms2.announcement

import frontlinesms2.*

class AnnouncementBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestAnnouncements() {
		Announcement.build(name:'New Office', sentMessageText:"We are opening a new office in Kitali next week!")
		Announcement.build(name:'Office Party', sentMessageText:"Office Party on Friday!")
	}

	def createTestMessages() {
		Fmessage.build(src:'Max', text:'I will be late', date:new Date()-4, starred:true)
		Fmessage.build(src:'Jane', text:'Meeting at 10 am', date:new Date()-3)
		Fmessage.build(src:'Patrick', text:'Project has started', date:new Date()-2)
		Fmessage.build(src:'Zeuss', text:'Sewage blocked', date:new Date()-1)

		def newOffice = Announcement.findByName('New Office')
		['Max', 'Jane'].each { newOffice.addToMessages(Fmessage.findBySrc(it)) }
		newOffice.save(failOnError:true, flush:true)

		def officeParty = Announcement.findByName('Office Party')
		['Zeuss', 'Patrick'].each { officeParty.addToMessages(Fmessage.findBySrc(it)) }
		officeParty.save(failOnError:true, flush:true)
	}
}

