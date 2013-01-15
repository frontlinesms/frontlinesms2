package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.buildtestdata.mixin.Build

@TestFor(SubscriptionService)
@Build([Group, SmartGroup, Contact, Fmessage, Subscription, GroupMembership])
class SubscriptionServiceSpec extends Specification {
	def s, c, g, service

	private static final String TEST_CONTACT = '+1111111111'
	private static final String TEST_NON_CONTACT = '+2222222222'

	def setup() {
		service = new SubscriptionService()
		createTestSubscriptionAndGroup()
		createTestContact()
		Subscription.metaClass.addToMessages  = { m -> m }
	}

	def 'join autoreply message should be sent when doJoin is triggered'() {
		given:
			def sendService = Mock(MessageSendService)
			s.messageSendService = sendService

			def replyMessage = mockFmessage("woteva")
			sendService.createOutgoingMessage({ params ->
				params.addresses==TEST_CONTACT && params.messageText=='you have joined'
			}) >> replyMessage
		when:
			service.doJoin(s, mockFmessage("doesntmatter"))
		then:
			1 * sendService.send(replyMessage)
	}

	def 'leave autoreply message should be sent when doLeave is triggered'() {
		given:
			def sendService = Mock(MessageSendService)
			s.messageSendService = sendService

			def replyMessage = mockFmessage("woteva")
			sendService.createOutgoingMessage({ params ->
				params.addresses==TEST_CONTACT && params.messageText=='you have left'
			}) >> replyMessage
		when:
			service.doLeave(s, mockFmessage("doesntmatter"))
		then:
			1 * sendService.send(replyMessage)
	}

	def 'correct autoreply message for join should be sent when toggle action is triggered'() {
		given:
			def sendService = Mock(MessageSendService)
			s.messageSendService = sendService

			def replyMessage = mockFmessage("woteva")
			sendService.createOutgoingMessage({ params ->
				params.addresses==TEST_CONTACT && params.messageText=='you have joined'
			}) >> replyMessage
		when:
			service.doToggle(s, mockFmessage("doesntmatter"))
		then:
			1 * sendService.send(replyMessage)
	}

	def 'correct autoreply message for leave should be sent when toggle action is triggered'() {
		given:
			def sendService = Mock(MessageSendService)
			s.messageSendService = sendService
			c.addToGroups(g)

			def replyMessage = mockFmessage("woteva")
			sendService.createOutgoingMessage({ params ->
				params.addresses==TEST_CONTACT && params.messageText=='you have left'
			}) >> replyMessage
		when:
			service.doToggle(s, mockFmessage("doesntmatter"))
		then:
			1 * sendService.send(replyMessage)
	}

	private def createTestSubscriptionAndGroup() {
		g = new Group(name:"Subscription Group").save()
		s = new Subscription(name:"test subscription", group:g, joinAliases:"join", joinAutoreplyText:"you have joined", leaveAutoreplyText:"you have left", leaveAliases:"leave").save()
	}

	//> HELPERS
	private def processKeyword(String messageText, String sourcePhoneNumber, String ownerDetail) {
		def k = Mock(Keyword)
		k.ownerDetail >> ownerDetail
		s.processKeyword(mockMessage(messageText, sourcePhoneNumber), k)
	}

	private def createTestContact() {
		c = Contact.build(mobile:TEST_CONTACT)
	}

	private def mockMessage(String messageText, String sourcePhoneNumber) {
		return Fmessage.build(text:messageText, src:sourcePhoneNumber)
	}

	private def mockFmessage(String messageText, String src=null) {
		Fmessage m = Mock()
		m.text >> messageText
		m.src >> src
		return m
	}
}
