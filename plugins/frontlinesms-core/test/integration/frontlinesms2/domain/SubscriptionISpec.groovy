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

	@Unroll
	def 'keyword should issue proper Action'(){
		setup:
			s.defaultAction = Subscription.Action.TOGGLE
			s.save(failOnError:true)
		expect:
			action == s.getAction(s.keywords.find{ it.value == keyword})
		where:
			keyword|action
			"KEY"|Subscription.Action.TOGGLE
			"JOIN"|Subscription.Action.JOIN
			"IN"|Subscription.Action.JOIN
			"LEAVE"|Subscription.Action.LEAVE
			"OUT"|Subscription.Action.LEAVE
	}

	@Unroll
	def 'triggering keyword should change membership properly of contact'() {
		setup:
			s.defaultAction = Subscription.Action.TOGGLE
			s.save(failOnError:true)
			makeMember?g.addToMembers(c):null
		when:
			 s.processKeyword(mockMessage('KEY JOIN', TEST_CONTACT), s.keywords.find{ it.value == keyword})
		then:
			inGroup == c.isMemberOf(g)
		where:
			keyword|makeMember|inGroup
			"KEY"|true|false
			"JOIN"|true|true
			"IN"|true|true
			"LEAVE"|true|false
			"OUT"|true|false
			// if contact is not a member of group
			"KEY"|false|true
			"JOIN"|false|true
			"IN"|false|true
			"LEAVE"|false|false
			"OUT"|false|false
	}

	@Unroll
	def 'triggering keyword should change membership properly of non-contact'() {
		setup:
			s.defaultAction = Subscription.Action.TOGGLE
			s.save(failOnError:true)
		when:
			 s.processKeyword(mockMessage('KEY JOIN', TEST_NON_CONTACT), s.keywords.find{ it.value == keyword})
		then:
			inGroup == g.getMembers().id.contains(getNewContact()?.id)
		where:
			keyword|inGroup
			"KEY"|true
			"JOIN"|true
			"IN"|true
			"LEAVE"|false
			"OUT"|false
	}

	@Unroll
	def 'Correct Action is triggered depending on the defaultAction set for the subscription'(){
		setup:
			s.defaultAction = defaultAction
			s.save(failOnError:true)
		expect:
			action == s.getAction(s.keywords.find{ it.value == keyword})
		where:
			keyword|defaultAction|action
			'KEY'|Subscription.Action.JOIN|Subscription.Action.JOIN
			'KEY'|Subscription.Action.LEAVE|Subscription.Action.LEAVE
			'KEY'|Subscription.Action.TOGGLE|Subscription.Action.TOGGLE
	}

	private def createTestContact() {
		c = Contact.build(mobile:TEST_CONTACT)
	}

	private def processKeyword(String messageText, String sourcePhoneNumber, boolean exactMatch=true) {
		s.processKeyword(mockMessage(messageText, sourcePhoneNumber), exactMatch)
	}

	private def createTestSubscriptionAndGroup() {
		g = new Group(name:"Subscription Group").save(failOnError:true)
		def k0  = new Keyword(value:"KII", ownerDetail:null)
		def k1  = new Keyword(value:"KEY", ownerDetail:null)
		def k2  = new Keyword(value:"JOIN", ownerDetail:Subscription.Action.JOIN.toString())
		def k3  = new Keyword(value:"IN", ownerDetail:Subscription.Action.JOIN.toString())
		def k4  = new Keyword(value:"LEAVE", ownerDetail:Subscription.Action.LEAVE.toString())
		def k5  = new Keyword(value:"OUT", ownerDetail:Subscription.Action.LEAVE.toString())

		s = new Subscription(name:"test subscription", group:g, joinAliases:"join", joinAutoreplyText:"you have joined", leaveAutoreplyText:"you have left", leaveAliases:"leave")
		s.addToKeywords(k0)
		s.addToKeywords(k1)
		s.addToKeywords(k2)
		s.addToKeywords(k3)
		s.addToKeywords(k4)
		s.addToKeywords(k5)
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

