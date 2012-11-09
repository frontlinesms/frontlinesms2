package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import grails.buildtestdata.mixin.Build

@TestFor(Autoforward)
@Mock([Keyword, SmartGroup, Group, Contact, Fmessage, MessageSendService])
@Build([Contact, Autoforward])
class AutoforwardSpec extends Specification {
	private static final String TEST_NUMBER = "+2345678"
	
	def setup() {
		// Not sure why this is necessary with Test Mixins, but it seems to be:
		Autoforward.metaClass.addToMessages = { m ->
			if(delegate.messages) delegate.messages << m
			else delegate.messages = [m]
			return delegate
		}
	}

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

	@Unroll
	def 'getRecipientCount() should count group members and contacts'() {
		given:
			def a = new Autoforward(name:'recipient counter')
			if(contacts) a.contacts = (1..contacts).collect { Contact.build() }
			if(groups) a.groups = (1..groups).collect { mockGroup(groupMembers) }
			def sg = []
			if(smartGroups) sg = (1..smartGroups).collect { def smrt = mockSmartGroup(smartGroupMembers); println "smrt.members=$smrt.members"; return smrt }
			println "sg = $sg"
			println "sg.mem = ${sg*.members}"
			sg.each {
				println "it.members = $it.members"
			}
			a.smartGroups = sg
			if(smartGroups) println "membaz: " + a.smartGroups*.members
		expect:
			a.recipientCount == expectedRecipientCount
		where:
			expectedRecipientCount | contacts | groups | groupMembers | smartGroups | smartGroupMembers
			1                      | 1        | 0      | 0            | 0           | 0
			0                      | 0        | 1      | 0            | 0           | 0
			1                      | 0        | 1      | 1            | 0           | 0
			10                     | 0        | 2      | 5            | 0           | 0
			12                     | 2        | 2      | 5            | 0           | 0
			0                      | 0        | 0      | 0            | 1           | 0
			1                      | 0        | 0      | 0            | 1           | 1
			10                     | 0        | 0      | 0            | 5           | 2
			12                     | 2        | 0      | 0            | 5           | 2
			22                     | 2        | 2      | 5            | 5           | 2
	}
@spock.lang.IgnoreRest
	def "the outgoing message created by processKeyword should have the owner detail set to the id of the triggering incoming message"() {
		setup:
			def outgoigMessage = mockFmessage("text","23423")
			def autoforward = Autoforward.build(contacts:[Contact.build(mobile:TEST_NUMBER)], sentMessageText:'some forward text')
			def sendService = Mock(MessageSendService)
			sendService.createOutgoingMessage([contacts:autoforward.contacts, groups:autoforward.groups?:[] + autoforward.smartGroups?:[], messageText:autoforward.sentMessageText]) >> { println "I was called here as well"; outgoigMessage }
			autoforward.messageSendService = sendService
			def inMessage = mockFmessage("message text", '+123457890', "nully")
		when:	
			autoforward.processKeyword(inMessage, Mock(Keyword))
		then:
			1 * outgoigMessage.setOwnerDetail(_)
	}

	private def mockContact() { Contact.build() }

	private def mockGroup(int memberCount) {
		mockMembers(Group, memberCount)
	}

	private def mockSmartGroup(int memberCount) {
		mockMembers(SmartGroup, memberCount)
	}

	private def mockMembers(gClass, memberCount) {
		def g = Mock(gClass)
		if(memberCount) {
			def members = (1..memberCount).collect { Contact.build() }
			println "mockMembers() :: members=$members"
			g.members >> { members }
			println "mockMembers() :: g.members=$g.members"
			println "mockMembers() :: g.members=$g.members"
			println "mockMembers() :: g.members=$g.members"
			println "mockMembers() :: g.members=$g.members"
		}
		return g
	}

	private def mockFmessage(String messageText, String src=null, String ownerDetail=null) {
		Fmessage m = Mock()
		m.id >> 1
		m.text >> messageText
		m.src >> src
		m.setOwnerDetail >> ownerDetail
		return m
	}
}

