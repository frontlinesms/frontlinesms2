package frontlinesms2

class AnnouncementSpec extends grails.plugin.spock.UnitSpec {
	def "Announcement has a name and a sent message"() {
		given:
			mockDomain(Announcement)
		when:
			def a = new Announcement()
		then:
			!a.validate()
		when:
			a.name = 'test announcement'
		then:
			!a.validate()
		when:
			a.sentMessage = ''
		then:
			!a.validate()
		when:
			a.sentMessage = 'test'
		then:
			a.validate()
	}
	
	def "Announcement can have one or many messages"() {
		given:
			mockDomain(Announcement)
		when:
			def a = new Announcement(name:'test announcement', sentMessage: 'test')
		then:
			a.validate()
		when:
			a.addToMessages(new Fmessage())
		then:
			a.validate()
		when:
			a.addToMessages(new Fmessage())
		then:
			a.validate()
	}
}

