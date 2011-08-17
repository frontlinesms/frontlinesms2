package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class FmessageSpec extends UnitSpec {
	def 'check that READ flag cannot be null'() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			Fmessage message = new Fmessage(read: null)
		then:
			message.read != null || !message.validate()
	}
	
	def 'check that messages are unread by default'() {
		when:
			Fmessage message = new Fmessage()
		then:
			message.read == false
	}
	
	def 'deleting message sets deleted flag to true'() {
		when:
			Fmessage message = new Fmessage()
		then:
			message.deleted == false
		when:
			message.toDelete()
		then:
			message.deleted == true
	}

	def 'messages are unstarred by default'() {
		when:
			Fmessage message = new Fmessage()
		then:
			message.starred == false
	}

	def 'starring messages sets the star flag to true'() {
		when:
			Fmessage message = new Fmessage()
		then:
			message.starred == false
		when:
			message.addStar()
		then:
			message.starred == true
	}
	
	def 'unstarring messages sets the star flag to false'() {
		when:
			Fmessage message = new Fmessage()
		then:
			message.addStar()
		when:
			message.removeStar()
		then:
			message.starred == false
	}

	def "Fmessage can have null status"() {
		setup:
			mockDomain(Fmessage)
		when:
			def m = new Fmessage(status: null)
		then:
			m.validate()
	}

	def "should return count as zero if no search string is given"() {
		when:
			def count = Fmessage.countAllSearchMessages()
		then:
			count == 0
	}

	def 'archiving messages sets the archived flag to true'() {
		when:
			Fmessage message = new Fmessage()
		then:
			message.archived == false
		when:
			message.archive()
		then:
			message.archived == true
	}

	def "should fetch the display name of the recipient"() {
		setup:
			mockDomain Contact, [new Contact(name: "John", primaryMobile: "dst")]
			mockDomain Fmessage, [new Fmessage(src: "src", dst: "dst"), new Fmessage(src: "src1", dst: "dst1")]
		when:
			def message = Fmessage.findBySrc("src")
			def message1 = Fmessage.findBySrc("src1")
		then:
			message.getRecipientDisplayName() == "John"
			message1.getRecipientDisplayName() == "dst1"

	}
}

