package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class FmessageSpec extends UnitSpec {
	
	def 'READ flag cannot be null'() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			Fmessage message = new Fmessage(src: '21345', read: null, inbound: true)
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

	def "Fmessage must have a src if inbound"() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			def m = new Fmessage(inbound:true)
		then:
			!m.validate()
		when:
			def t = new Fmessage(src: 'src', inbound: true)
		then:
			t.validate()
	}

	@Unroll
	def "outbound message must have one or more dispatches"() {
		given:
			mockForConstraintsTests(Fmessage)
		expect:
			new Fmessage(dispatches:dispatches).validate() == valid
		where:
			valid | dispatches
			false | []
			false | null
			true  | [new Dispatch()]
			true  | [new Dispatch(), new Dispatch()]
	}
	
	def "inbound Fmessages cannot have dispatches"() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			Fmessage message = new Fmessage(src: '23456', inbound: true, dispatches: [new Dispatch()])
		then:
			!message.validate()
	}
	
	def 'message can have an activity'() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			def message = new Fmessage(src: 'src', inbound: true, messageOwner: new Folder(archived: false))
		then:
			message.validate()
	}
}

