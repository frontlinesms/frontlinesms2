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

	def "should return true if there are failed messages"() {
		setup:
			registerMetaClass(Fmessage)
			Fmessage.metaClass.'static'.getPendingMessages = {params -> [new Fmessage(status: MessageStatus.SEND_FAILED), new Fmessage(status: MessageStatus.SEND_PENDING)]}
		when:
			def result = Fmessage.hasUndeliveredMessages()
		then:
			result
	}

	def "should return false if there are no failed messages"() {
		setup:
			registerMetaClass(Fmessage)
			Fmessage.metaClass.'static'.getPendingMessages = {params -> [new Fmessage(status: MessageStatus.SEND_PENDING), new Fmessage(status: MessageStatus.SEND_PENDING)]}
		when:
			def result = Fmessage.hasUndeliveredMessages()
		then:
			!result
	}
}

