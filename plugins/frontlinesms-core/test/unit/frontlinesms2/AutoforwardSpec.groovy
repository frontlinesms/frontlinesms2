package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import grails.buildtestdata.mixin.Build

@TestFor(Autoforward)
@Mock([Keyword, SmartGroup, Group, Contact])
@Build([Contact, Autoforward])
class AutoforwardSpec extends Specification {
	private static final String TEST_NUMBER = "+2345678"

	@Unroll
	def "Test Constraints"() {
		when:
			def autoforward = new Autoforward()
			props.each { k, v -> autoforward[k] = v }
			println "FORWARD.contacts=$autoforward.contacts; props=$props"
			autoforward.validate()
			println autoforward.errors
		then:
			autoforward.validate() == valid
		where:
			valid | props
			false | [name:"name"]
			false | [name:"name", keywords:[new Keyword(value:"keyword")]]
			true  | [name:"name", contacts:[new Contact(name:"name")]]
			true  | [name:"name", contacts:[new Contact(name:"name")], keywords:[new Keyword(value:"keyword")]]
			true  | [name:"name", smartGroups:[new SmartGroup(name:"SmartGroup", contactName:"contactName")], keywords:[new Keyword(value:"keyword")]]
			true  | [name:"name", groups:[new Group(name:"test")], keywords:[new Keyword(value:"keyword")]]
			true  | [name:"name", contacts:[new Contact(name:"name")], groups:[new Group(name:"test")], smartGroups:[new SmartGroup(name:"SmartGroup", contactName:"contactName")], keywords:[new Keyword(value:"keyword")]]
	}

	def 'processKeyword should send a message if exact match is found and activity'() {
		given:
			def autoforward = Autoforward.build(contacts:[Contact.build(mobile:TEST_NUMBER)], sentMessageText:'some forward text')
			def sendService = Mock(MessageSendService)
			autoforward.messageSendService = sendService

			def forwardMessage = mockFmessage("message text")
			sendService.createOutgoingMessage({ params ->
				params.contacts*.mobile==[TEST_NUMBER] && params.messageText=='some forward text'
			}) >> forwardMessage

			def inMessage = mockFmessage("message text", '+123457890')
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

