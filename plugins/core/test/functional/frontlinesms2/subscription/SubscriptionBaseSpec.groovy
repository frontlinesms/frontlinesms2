package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.Subscription.Action

class SubscriptionBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestSubscriptions() {
println "All memberships: ${GroupMembership.findAll()}"
println "All groups: ${Group.findAll()}"
println "All contacts: ${Contact.findAll()}"
		def allrounderBobby = Contact.build(mobile:"987654321")
		def camperSam = Contact.build(mobile:"987654322")
		def footyRon = Contact.build(mobile:"987654323")

		def campingGroup = Group.build(name:"Camping Group")
		def campingKeyword = new Keyword(value: 'CAMPING')
		def campingSub = new Subscription(name:"Camping Subscription", group:campingGroup, joinAliases:"JOIN,IN,START", leaveAliases:"LEAVE,OUT,STOP",
				defaultAction:Action.JOIN, keyword:campingKeyword).save(failOnError:true)
		campingGroup.addToMembers(allrounderBobby)
		campingGroup.addToMembers(camperSam)

		campingGroup.save(failOnError:true)

		def footballGroup = Group.build(name:"Football Updates")
		def footballKeyword = new Keyword(value: 'FOOTBALL')
		def footballSub = new Subscription(name:"Football Updates Subscription", group:campingGroup, joinAliases:"JOIN,IN,START", leaveAliases:"LEAVE,OUT,STOP",
				defaultAction:Action.JOIN, keyword:footballKeyword).save(failOnError:true)
		
		footballGroup.addToMembers(allrounderBobby)
		footballGroup.addToMembers(footyRon)

		footballGroup.save(failOnError:true)
	}

	static createTestMembers(Group g) {
		(1..90).each {
			def c = Contact.build(mobile:"987654321${it}")
			c.addToGroups(g)
		}
	}

	static createTestMessages(Subscription s) {
		(11..90).each {
			def m = Fmessage.build(src:'Bob', text:'Test message number${it}', date:new Date()-it)
			s.addToMessages(m) // TODO correct this to sort messages into either join or leave
			s.save(failOnError:true, flush:true)
		}
	}

	static createTestActivities() {
		Announcement.build(name:"Sample Announcement", sentMessageText:"Message to send")
	}
}
