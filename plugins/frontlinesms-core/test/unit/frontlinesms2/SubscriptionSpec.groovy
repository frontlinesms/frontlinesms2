package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

import grails.buildtestdata.mixin.Build

@TestFor(Subscription)
@Mock([Group, GroupMembership, SubscriptionService])
@Build([Contact, Fmessage])
class SubscriptionSpec extends Specification {

	private static final String TEST_CONTACT = '+1111111111'
	private static final String TEST_NON_CONTACT = '+2222222222'

	def s, c, g, subscriptionService, m

	def setup() {
		createTestSubscriptionAndGroup()
		createTestContact()
		Subscription.metaClass.addToMessages  = { m -> m }
	}

	def 'subscriptionService.doJoin is called when processJoin is called'() {
		when:
			m = processKeyword("KEY JOIN", TEST_CONTACT, "JOIN")
		then:
			1 * subscriptionService.doJoin(s, m)
	}

	def 'subscriptionService.doLeave is called when processLeave is called'() {
		when:
			m = processKeyword("KEY LEAVE", TEST_CONTACT, "LEAVE")
		then:
			1 * subscriptionService.doLeave(s, m)
	}

	def 'subscriptionService.doToggle is called when processToggle is called'() {
		when:
			m = processKeyword("KEY TOGGLE", TEST_CONTACT, "TOGGLE")
		then:
			1 * subscriptionService.doToggle(s, m)
	}

	private def createTestSubscriptionAndGroup() {
		g = new Group(name:"Subscription Group").save()
		s = new Subscription(name:"test subscription", group:g, joinAliases:"join", joinAutoreplyText:"you have joined", leaveAutoreplyText:"you have left", leaveAliases:"leave")
		subscriptionService = Mock(SubscriptionService)
		s.subscriptionService = subscriptionService
	}

	//> HELPERS
	private def processKeyword(String messageText, String sourcePhoneNumber, String ownerDetail) {
		def k = Mock(Keyword)
		k.ownerDetail >> ownerDetail
		def m = mockMessage(messageText, sourcePhoneNumber)
		s.processKeyword(m, k)
		m
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