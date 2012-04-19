package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(Announcement)
@Mock([MessageOwner, Fmessage])
class AnnouncementSpec extends Specification {
	def "Announcement must have a name and a sent message"() {
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
/* this test breaks something in spock	
	def "Announcement can have one or many messages"() {
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
	} */
}

