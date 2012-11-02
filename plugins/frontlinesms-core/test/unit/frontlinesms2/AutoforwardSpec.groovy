package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(Autoforward)
@Mock([Keyword, SmartGroup, Group, Contact])
class AutoforwardSpec extends Specification {
	private static final String TEST_NUMBER = "+2345678"

	@Unroll
	def "Test Constraints"() {
		when:
			def autoforward = new Autoforward()
			autoforward.properties = props
		then:
			autoforward.validate() == valid
		where:
			valid | props
			false | [name:"name", contacts:null, groups:null, smartGroups:null, keyword:null]
			false | [name:"name", contacts:null, groups:null, smartGroups:null, keyword:new Keyword(value:"keyword")]
			true  | [name:"name", contacts:[new Contact(name:"name")], groups:null, smartGroups:null, keyword:null]
			true  | [name:"name", contacts:[new Contact(name:"name")], groups:null, smartGroups:null, keyword:new Keyword(value:"keyword")]
			true  | [name:"name", contacts:null, groups:null, smartGroups:[new SmartGroup(name:"SmartGroup", contactName:"contactName")], keyword:new Keyword(value:"keyword")]
			true  | [name:"name", contacts:null, groups:[new Group(name:"test")], smartGroups:null, keyword:new Keyword(value:"keyword")]
			true  | [name:"name", contacts:[new Contact(name:"name")], groups:[new Group(name:"test")], smartGroups:[new SmartGroup(name:"SmartGroup", contactName:"contactName")], keyword:new Keyword(value:"keyword")]
	}

	def 'processKeyword should send a message if exact match is found'() {
		given:
			def autoforward = new Autoforward(name:'whatever', contacts:new Contact(name:"name"))
			def sendService = Mock(MessageSendService)
			autoforward.messageSendService = sendService

			def forwardMessage = mockFmessage("message text")
			sendService.createOutgoingMessage({ params ->
				params.addresses==TEST_NUMBER && params.messageText=='some forward text'
			}) >> forwardMessage

			def inMessage = mockFmessage("message text", TEST_NUMBER)
		when:
			autoforward.processKeyword(inMessage, Mock(Keyword))
		then:
			1 * sendService.send(forwardMessage)
	}

	private def mockFmessage(String messageText, String src=null) {
		Fmessage m = Mock()
		m.text >> messageText
		m.src >> src
		return m
	}
}