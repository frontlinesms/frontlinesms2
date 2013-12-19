package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(TextMessage)
class TextMessageSpec extends Specification {
	def 'TEXT cannot be null'() {
		when:
			TextMessage message = new TextMessage(text:null, src:'21345', read:true, inbound: true)
		then:
			!message.validate()
	}

	def 'READ flag cannot be null'() {
		when:
			TextMessage message = new TextMessage(src: '21345', read: null, inbound: true)
		then:
			message.read != null || !message.validate()
	}
	
	def 'messages are unread by default'() {
		when:
			TextMessage message = new TextMessage()
		then:
			message.read == false
	}
	
	def 'messages are unstarred by default'() {
		when:
			TextMessage message = new TextMessage()
		then:
			message.starred == false
	}

	def "TextMessage must have a src if inbound"() {
		when:
			def m = new TextMessage(inbound:true)
		then:
			!m.validate()
		when:
			def t = new TextMessage(text:'text', src: 'src', inbound: true)
		then:
			t.validate()
	}

	@Unroll
	def "outbound message must have one or more dispatches"() {
		expect:
			new TextMessage(text:'text', dispatches:dispatches).validate() == valid
		where:
			valid | dispatches
			false | []
			false | null
			true  | [new Dispatch()]
			true  | [new Dispatch(), new Dispatch()]
	}
	
	def "inbound TextMessages cannot have dispatches"() {
		when:
			TextMessage message = new TextMessage(src: '23456', inbound: true, dispatches: [new Dispatch()])
		then:
			!message.validate()
	}
	
	def 'message can have an activity'() {
		when:
			def message = new TextMessage(text:'text', src: 'src', inbound: true, messageOwner: new Folder(archived: false))
		then:
			message.validate()
	}
}

