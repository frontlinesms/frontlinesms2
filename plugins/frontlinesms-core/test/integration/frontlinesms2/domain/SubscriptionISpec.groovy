package frontlinesms2.domain

import frontlinesms2.*
import frontlinesms2.Subscription.Action
import spock.lang.*

class SubscriptionISpec extends grails.plugin.spock.IntegrationSpec {
	private static final String TEST_CONTACT = '+1111111111'
	private static final String TEST_NON_CONTACT = '+2222222222'

	def s, c, g

	def setup() {
		createTestSubscriptionAndGroup()
		createTestContact()
	}

	def 'triggering join keyword for an existing contact should add him to a group'() {
		when:
			processKeyword('KEY JOIN', TEST_CONTACT)
		then:
			c.isMemberOf(g)
	}

	def 'triggering leave keyword for an existing contact should remove him from a group if he is already a member'() {
		given:
			g.addToMembers(c)
		when:
			processKeyword('KEY LEAVE', TEST_CONTACT)
		then:
			!c.isMemberOf(g)
	}

	def 'triggering leave keyword for an existing contact should do nothing if he is not already a member'() {
		when:
			processKeyword('KEY LEAVE', TEST_CONTACT)
		then:
			!c.isMemberOf(g)
	}

	def 'triggering join keyword for a non-existing contact should create him and add him to the group'() {
		when:
			processKeyword('KEY JOIN', TEST_NON_CONTACT)
		then:
			getNewContact()
		and:
			getNewContact().isMemberOf(g)
	}

	def 'triggering leave keyword for a non-existing contact should have no effect'() {
		when:
			processKeyword('KEY LEAVE', TEST_NON_CONTACT)
		then:
			!getNewContact()	
	}

	def 'triggering toggle for an existing contact should add him to a group if he is not already a member'() {
		when:
			processKeyword('KEY', TEST_CONTACT)
		then:
			c.isMemberOf(g)
	}

	def 'triggering toggle for an existing contact should remove him from a group if he is already a member'() {
		given:
			g.addToMembers(c)
		when:
			processKeyword('KEY', TEST_CONTACT)
		then:
			!c.isMemberOf(g)
	}

	def 'triggering toggle for a non-existing contact should create him and add him to the group'() {
		when:
			processKeyword('KEY', TEST_NON_CONTACT)
		then:
			getNewContact()
		and:
			getNewContact().isMemberOf(g)
	}

	@Unroll
	def 'exact matches should map to alias'() {
		expect:
			s.getAction(messageText, true) == action
		where:
			messageText | action
			'KEY JOIN'  | Action.JOIN
			'KEY LEAVE' | Action.LEAVE
			'KEY'       | Action.TOGGLE
	}

	@Unroll
	def 'non-exact matches should map to alias'() {
		expect:
			s.getAction(messageText, false) == action
		where:
			messageText | action
			'KEYJOIN'   | Action.JOIN
			'KEYLEAVE'  | Action.LEAVE
	}

	@Unroll
	def 'exact match without alias match should map to blank setting'() {
		expect:
			s.getAction(messageText, true) == Action.TOGGLE
		where:
			messageText << ['KEY SOMETHING', 'KEY OTHERWISE', 'KEY RAMBLING NONSENSE']
	}

	@Unroll
	def 'non-exact match without alias should not map'() {
		expect:
			s.getAction(messageText, false) == null
		where:
			messageText << ['KEY SOMETHING', 'KEY OTHERWISE', 'KEY RAMBLING NONSENSE']
	}

//> HELPERS
	private def processKeyword(String messageText, String sourcePhoneNumber, boolean exactMatch=true) {
		s.processKeyword(mockMessage(messageText, sourcePhoneNumber), exactMatch)
	}

	private def createTestContact() {
		c = Contact.build(mobile:TEST_CONTACT)
	}

	private def createTestSubscriptionAndGroup() {
		g = new Group(name:"Subscription Group").save()
		def keyword = new Keyword(value:"KEY")
		s = new Subscription(name:"test subscription", keyword: keyword,group:g, joinAliases:"join", joinAutoreplyText:"you have joined", leaveAutoreplyText:"you have left", leaveAliases:"leave")
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

	private def getNewContact() {
		Contact.findByMobile(TEST_NON_CONTACT)
	}
}

