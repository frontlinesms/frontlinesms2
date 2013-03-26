package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

import grails.buildtestdata.mixin.Build

@TestFor(Subscription)
@Mock([Group, GroupMembership, SubscriptionService])
@Build([Contact, Fmessage])
class SubscriptionSpec extends Specification {
	private static final String TEST_CONTACT = '+1111111111'

	def s, c, g, subscriptionService, m

	def setup() {
		createTestSubscriptionAndGroup()
		createTestContact()
		Subscription.metaClass.addToMessages  = { m -> m }
	}

	def 'subscriptionService.doJoin is called when processJoin is called'() {
		when:
			processKeyword("KEY JOIN", TEST_CONTACT, "JOIN")
		then:
			1 * subscriptionService.doJoin(_, _)
	}

	def 'subscriptionService.doLeave is called when processLeave is called'() {
		when:
			processKeyword("KEY LEAVE", TEST_CONTACT, "LEAVE")
		then:
			1 * subscriptionService.doLeave(_, _)
	}

	def 'subscriptionService.doToggle is called when processToggle is called'() {
		when:
			processKeyword("KEY TOGGLE", TEST_CONTACT, "TOGGLE")
		then:
			1 * subscriptionService.doToggle(_, _)
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
}

