package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import grails.buildtestdata.mixin.Build

@TestFor(Announcement)
@Mock([Fmessage])
@Build(Fmessage)
class AnnouncementSpec extends Specification {
	def setup() {
		// Not sure why this is necessary with Test Mixins, but it seems to be:
		Announcement.metaClass.addToMessages = { m ->
			if(!delegate.messages) delegate.messages = [m]
			else delegate.messages.add(m)
			return delegate
		}
	}

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
			a.addToMessages(Fmessage.build())
		then:
			a.validate()
	}
/* this test breaks something in spock	*/
	def "Announcement can have one or many messages"() {
		when:
			def a = new Announcement(name:'test announcement')
					.addToMessages(Fmessage.build())
		then:
			a.validate()
		when:
			a.addToMessages(Fmessage.build())
		then:
			a.validate()
		when:
			a.addToMessages(Fmessage.build())
		then:
			a.validate()
	} /**/
}

