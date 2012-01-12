package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class FmessageSpec extends UnitSpec {
	
	def 'READ flag cannot be null'() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			Fmessage message = new Fmessage(read: null, date: new Date(), inbound: true)
		then:
			message.read != null || !message.validate()
	}
	
	def 'messages are unread by default'() {
		when:
			Fmessage message = new Fmessage()
		then:
			message.read == false
	}
	
	def 'messages are unstarred by default'() {
		when:
			Fmessage message = new Fmessage()
		then:
			message.starred == false
	}

	def "Fmessage cannot be inbound and have a status"() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			def m = new Fmessage(status: null,  date: new Date(), inbound: false)
		then:
			!m.validate()
		when:
			m.inbound = true
		then:
			m.validate()
		when:
			m.status = FmessageStatus.HASFAILED
		then:
			!m.validate()
	}
	
	def "Fmessage must have a date"() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			def m = new Fmessage(inbound: true)
		then:
			!m.validate()
		when:
			def f = new Fmessage(inbound: true, date: new Date())
		then:
			f.validate()
	}
	
	def "Fmessages with a status must have a least 1 Dispatch"() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			Fmessage message = new Fmessage(date: new Date(), status: FmessageStatus.HASFAILED)
		then:
			!message.validate()
		when:
			Fmessage message2 = new Fmessage(date: new Date(), status: FmessageStatus.HASFAILED, dispatches: [new Dispatch()])
		then:
			message2.validate()
	}
	
	def "Fmessages can have multiple dispatches"() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			Fmessage message = new Fmessage(date: new Date(), status: FmessageStatus.HASFAILED, dispatches: [new Dispatch(), new Dispatch()])
		then:
			message.validate()
	}
	
	def "inbound Fmessages cannot have dispatches"() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			Fmessage message = new Fmessage(date: new Date(), inbound: true, dispatches: [new Dispatch()])
		then:
			!message.validate()
	}
	
	def 'message can have an activity'() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			def message = new Fmessage(date: new Date(), inbound: true, messageOwner: new Folder(archived: false))
		then:
			message.validate()
	}
}

