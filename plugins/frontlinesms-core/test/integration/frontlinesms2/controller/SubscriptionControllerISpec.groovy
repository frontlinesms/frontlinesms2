package frontlinesms2.controller

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class SubscriptionControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def trashService
	def i18nUtilService

	def setup() {
		controller = new SubscriptionController()
	}

	//> CED tests
	def 'can save a subscription'() {
		setup:
			controller.params.name = "Test"
			controller.params.subscriptionGroup = createTestGroup().id
			controller.params.topLevelKeywords = "subscription"
			controller.params.joinKeywords = "joining,in,tuko"
			controller.params.leaveKeywords = "stop,spam,out"
			controller.params.defaultAction = "toggle"
			controller.params.joinAutoreplyText = "welcome"
			controller.params.leaveAutoreplyText = 'bye bye'
		when:
			controller.save()
		then:
			def subscription = Subscription.findByName("Test")
			controller.flash.message == i18nUtilService.getMessage([code:"subscription.save.success", args:[subscription.name]])
			subscription.keywords.findAll { it.ownerDetail == "JOIN" && !it.isTopLevel }*.value.sort().join(',') == "IN,JOINING,TUKO"
			subscription.keywords.findAll { !it.ownerDetail && it.isTopLevel }*.value.sort().join(',') == "SUBSCRIPTION"
			subscription.keywords.findAll { it.ownerDetail == "LEAVE" && !it.isTopLevel }*.value.sort().join(',') == "OUT,SPAM,STOP"
			subscription.defaultAction == Subscription.Action.TOGGLE
			subscription.joinAutoreplyText == "welcome"
			subscription.leaveAutoreplyText == 'bye bye'
	}

	def 'top level keywords are optional'() {
		setup:
			controller.params.name = "Test"
			controller.params.subscriptionGroup = createTestGroup().id
			controller.params.joinKeywords = "joining,in,tuko"
			controller.params.leaveKeywords = "stop,spam,out"
			controller.params.defaultAction = "leave"
			controller.params.joinAutoreplyText = "welcome"
			controller.params.leaveAutoreplyText = 'bye bye'
		when:
			controller.save()
		then:
			def s = Subscription.findByName("Test")
			s.keywords.findAll { it.ownerDetail == "JOIN" && it.isTopLevel }*.value.sort().join(',') == "IN,JOINING,TUKO"
			s.keywords.findAll { it.ownerDetail == "LEAVE"  && it.isTopLevel }*.value.sort().join(',') == "OUT,SPAM,STOP"
			s.defaultAction == Subscription.Action.LEAVE
			s.joinAutoreplyText == "welcome"
			s.leaveAutoreplyText == 'bye bye'
	}

	def 'save also works for multiple top level keywords'() {
		setup:
			controller.params.name = "Test"
			controller.params.subscriptionGroup = createTestGroup().id
			controller.params.topLevelKeywords ="applE , bananA, CARROT JUICE "
			controller.params.joinKeywords = "joining,in,tuko"
			controller.params.leaveKeywords = "stop,spam,out"
			controller.params.defaultAction = "toggle"
			controller.params.joinAutoreplyText = "welcome"
			controller.params.leaveAutoreplyText = 'bye bye'
		when:
			controller.save()
		then:
			def s = Subscription.findByName("Test")
			s.keywords.findAll { !it.ownerDetail }*.value.sort().join(',') == "APPLE,BANANA,CARROTJUICE"
			s.keywords.findAll { it.ownerDetail == "JOIN" }*.value.sort().join(',') == "IN,JOINING,TUKO"
			s.keywords.findAll { it.ownerDetail == "LEAVE" }*.value.sort().join(',') == "OUT,SPAM,STOP"
			s.defaultAction == Subscription.Action.TOGGLE
			s.joinAutoreplyText == "welcome"
			s.leaveAutoreplyText == 'bye bye'
	}

	def 'can edit an existing subscription'() {
		setup:
			def group = createTestGroup()
			def s = new Subscription(name:"West", joinAutoreplyText:":)", leaveAutoreplyText:":(", defaultAction:Subscription.Action.TOGGLE, group:group)
			s.addToKeywords(new Keyword(value:"JEST", isTopLevel:true))
			s.addToKeywords(new Keyword(value:"YOURGROUP", isTopLevel:true))
			s.addToKeywords(new Keyword(value:"IN", isTopLevel:false, ownerDetail: "JOIN"))
			s.addToKeywords(new Keyword(value:"HERE", isTopLevel:false, ownerDetail: "JOIN"))
			s.addToKeywords(new Keyword(value:"OUT", isTopLevel:false, ownerDetail: "LEAVE"))
			s.addToKeywords(new Keyword(value:"GONE", isTopLevel:false, ownerDetail: "LEAVE"))
			s.save(failOnError: true)
			controller.params.ownerId = s.id
			controller.params.name = "Test"
			controller.params.joinKeywords = "joining,in,tuko"
			controller.params.leaveKeywords = "stop,spam,out"
			controller.params.defaultAction = "toggle"
			controller.params.joinAutoreplyText = "welcome"
			controller.params.leaveAutoreplyText = 'bye bye'
			controller.params.subscriptionGroup = group.id
		when:
			controller.save()
		then:
			def sub = Subscription.findByName("Test")
			sub.keywords.findAll { it.ownerDetail == "JOIN" }*.value.sort().join(',') == "IN,JOINING,TUKO"
			sub.keywords.findAll { it.ownerDetail == "LEAVE" }*.value.sort().join(',') == "OUT,SPAM,STOP"
			sub.defaultAction == Subscription.Action.TOGGLE
			sub.joinAutoreplyText == "welcome"
			sub.leaveAutoreplyText == 'bye bye'
	}

	//> Message Action tests (for messages moved into subscription)
	@Unroll
	def 'join, leave and toggle actions work, moving messsage to "join" and updating contact group membership'() {
		setup:
			def c = createTestContact()
			def g = createTestGroup(initiallyInGroup?c:null)
			def s = createTestSubscription(g)
			def m = createTestMessageFromContact("hello guys", c)
			s.addToMessages(m)
			s.save(failOnError:true)
			controller.params.ownerId = s.id
			controller.params.interactionId = m.id
		when:
			controller."${triggeredAction}"()
		then:
			(GroupMembership.getMembers(g, 5, 0) == [c]) == inGroupAtEnd
			m.refresh()
			m.ownerDetail == triggeredAction.toUpperCase()
		where:
			initiallyInGroup  |  triggeredAction  |  inGroupAtEnd
			true              |  "join"           |  true
			false             |  "join"           |  true
			true              |  "leave"          |  false
			false             |  "leave"          |  false
			true              |  "toggle"         |  false
			false             |  "toggle"         |  true
	}

	private def createTestSubscription(Group g) {
		def s = new Subscription(name:"West", joinAutoreplyText:":)", leaveAutoreplyText:":(", defaultAction: Subscription.Action.TOGGLE, group:g)
		s.addToKeywords(new Keyword(value:"IN", isTopLevel:false, ownerDetail: "JOIN"))
		s.addToKeywords(new Keyword(value:"OUT", isTopLevel:false, ownerDetail: "LEAVE"))
		s.save(failOnError: true)
		s
	}

	private def createTestGroup(Contact contact=null) {
		def g = new Group(name:"The Cool Gang").save(failOnError:true)
		if(contact)
			contact.addToGroup(g)
		g.save(failOnError:true)
		g
	}

	private def createTestContact() {
		def c = new Contact(name:"Bernard", mobile:"+123321").save(failOnError:true)
		c
	}

	private def createTestMessageFromContact(text, Contact c) {
		new TextMessage(text:text, src:c.mobile, inbound:true).save(failOnError:true)
	}

}
