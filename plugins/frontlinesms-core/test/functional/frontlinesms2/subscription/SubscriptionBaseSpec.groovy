package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.Subscription.Action

class SubscriptionBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestSubscriptions() {
		def allrounderBobby = Contact.build(mobile:"987654321")
		def camperSam = Contact.build(mobile:"987654322")
		def footyRon = Contact.build(mobile:"987654323")

		def campingGroup = Group.build(name:"Camping")
		def campingKeyword = new Keyword(value: 'CAMPING')
		def k1 = new Keyword(value:'JOIN', ownerDetail:'JOIN', isTopLevel:false)
		def k2 = new Keyword(value:'IN', ownerDetail:'JOIN', isTopLevel:false)
		def k3 = new Keyword(value:'START', ownerDetail:'JOIN', isTopLevel:false)
		def k4 = new Keyword(value:'LEAVE', ownerDetail:'LEAVE', isTopLevel:false)
		def k5 = new Keyword(value:'OUT', ownerDetail:'LEAVE', isTopLevel:false)
		def k6 = new Keyword(value:'STOP', ownerDetail:'LEAVE', isTopLevel:false)
		def campingSub = new Subscription(name:"Camping Subscription", group:campingGroup, defaultAction:Action.JOIN).save(failOnError:true, flush:true)
		campingSub.addToKeywords(campingKeyword)
		campingSub.addToKeywords(k1)
		campingSub.addToKeywords(k2)
		campingSub.addToKeywords(k3)
		campingSub.addToKeywords(k4)
		campingSub.addToKeywords(k5)
		campingSub.addToKeywords(k6)
		campingGroup.addToMembers(allrounderBobby)
		campingGroup.addToMembers(camperSam)

		campingGroup.save(failOnError:true, flush:true)

		def footballGroup = Group.build(name:"Football Updates")
		def footballKeyword = new Keyword(value: 'FOOTBALL')
		def k7 = new Keyword(value:'JOIN', ownerDetail:'JOIN', isTopLevel:false)
		def k8 = new Keyword(value:'IN', ownerDetail:'JOIN', isTopLevel:false)
		def k9 = new Keyword(value:'START', ownerDetail:'JOIN', isTopLevel:false)
		def k10 = new Keyword(value:'LEAVE', ownerDetail:'LEAVE', isTopLevel:false)
		def k11 = new Keyword(value:'OUT', ownerDetail:'LEAVE', isTopLevel:false)
		def k12 = new Keyword(value:'STOP', ownerDetail:'LEAVE', isTopLevel:false)
		def footballSub = new Subscription(name:"Football Updates Subscription", group:campingGroup, defaultAction:Action.JOIN).save(failOnError:true, flush:true)
		footballSub.addToKeywords(footballKeyword)
		footballSub.addToKeywords(k7)
		footballSub.addToKeywords(k8)
		footballSub.addToKeywords(k9)
		footballSub.addToKeywords(k10)
		footballSub.addToKeywords(k11)
		footballSub.addToKeywords(k12)
		footballGroup.addToMembers(allrounderBobby)
		footballGroup.addToMembers(footyRon)

		footballGroup.save(failOnError:true, flush:true)
	}

	static createTestMembers(Group g) {
		(1..90).each {
			def c = Contact.build(mobile:"987654321${it}")
			c.addToGroups(g)
		}
	}

	static createTestMessages(Subscription s) {
		(0..90).each {
			def m = Fmessage.build(src:'Bob', text:"Test message $it", date:new Date()-it)
			s.addToMessages(m) // TODO correct this to sort messages into either join or leave
		}
		s.save(failOnError:true, flush:true)
	}

	static createTestActivities() {
		Announcement.build(name:"Sample Announcement", sentMessageText:"Message to send")
		Fmessage.build(src:'announce')
	}
}

