package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.buildtestdata.mixin.Build

@TestFor(SubscriptionService)
@Build([Group, SmartGroup, Contact, TextMessage, Subscription, GroupMembership])
class SubscriptionServiceSpec extends Specification {
	def s, c, g, service, sendService, replyMessage

	private static final String TEST_CONTACT = '+1111111111'

	def setup() {
		service = new SubscriptionService()
		createTestContact()
		Subscription.metaClass.addToMessages  = { m -> m }
	}

	def 'join autoreply message should be sent when doJoin is triggered'() {
		given:
			createTestSubscription(true)
		when:
			service.doJoin(s, mockTextMessage("doesntmatter", TEST_CONTACT))
		then:
			1 * sendService.send(replyMessage)
	}

	def 'leave autoreply message should be sent when doLeave is triggered'() {
		given:
			createTestSubscription(false)
		when:
			service.doLeave(s, mockTextMessage("doesntmatter", TEST_CONTACT))
		then:
			1 * sendService.send(replyMessage)
	}

	def 'correct autoreply message for join should be sent when toggle action is triggered with a non-group member'() {
		given:
			createTestSubscription(true)
		when:
			service.doToggle(s, mockTextMessage("doesntmatter", TEST_CONTACT))
		then:
			1 * sendService.send(replyMessage)
	}

	def 'correct autoreply message for leave should be sent when toggle action is triggered with existing group member'() {
		given:
			createTestSubscription(false)
			c.addToGroup(g)
		when:
			service.doToggle(s, mockTextMessage("doesntmatter", TEST_CONTACT))
		then:
			1 * sendService.send(replyMessage)
	}

	private def createTestSubscription(joinExpected=true) {
		sendService = Mock(MessageSendService)
		replyMessage = mockTextMessage("woteva")
		sendService.createOutgoingMessage({ params ->
			println "CREATE OUTGOING MESSAGE INVOKED WITH PARAMS ::: $params"
			params.addresses==TEST_CONTACT && params.messageText == "you have ${joinExpected ? 'joined' : 'left'}"
		}) >> replyMessage
		g = new Group(name:"Subscription Group").save()
		s = Subscription.build(name:"test subscription", group:g, joinAutoreplyText:"you have joined", leaveAutoreplyText:"you have left")
		service.messageSendService = sendService
		s.save(failOnError:true)
	}

	//> HELPERS
	private def createTestContact() {
		c = Contact.build(mobile:TEST_CONTACT)
	}

	private def mockTextMessage(String messageText, String src=null) {
		TextMessage m = Mock()
		m.inbound >> true
		m.text >> messageText
		m.src >> src
		m.messageOwner >> Mock(Subscription)
		return m
	}
}
