package frontlinesms2

class AnnouncementSpec extends grails.plugin.spock.UnitSpec {
	def "Announcement must have a name and a sent message"() {
		given:
			mockDomain(MessageOwner)
			mockDomain(Announcement)
			mockDomain(Fmessage)
		when:
			def a = new Announcement()
		then:
			!a.validate()
		when:
			a.name = 'test announcement'
		then:
			!a.validate()
		when:
			a.addToMessages(new Fmessage(date: new Date(), inbound: true, src:'12345'))
		then:
			a.validate()
	}
	
	def "Announcement can have one or many messages"() {
		given:
			mockDomain(Announcement)
			mockDomain(Fmessage)
		when:
			def a = new Announcement(name:'test announcement', messages: [new Fmessage(date: new Date(), inbound: true, src:'12345')])
		then:
			a.validate()
		when:
			a.addToMessages(new Fmessage(date: new Date(), inbound: true, src:'12345'))
		then:
			a.validate()
		when:
			a.addToMessages(new Fmessage(date: new Date(), inbound: true, src:'12345'))
		then:
			a.validate()
	}
}

