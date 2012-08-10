package frontlinesms2.subscription

import frontlinesms2.*

class SubscriptionBaseSpec extends grails.plugin.geb.GebSpec {

	static createTestSubscriptions() {
		def campingGroup = new Group(name:"Camping Group").save()
		def campingSub = new Subscription(name:"Camping Subscription", group:campingGroup,
			keyword:"CAMP", joinAliases:"JOIN,IN,START", leaveAliases:"LEAVE,OUT,STOP",
			defaultAction:"toggle")

		def footballGroup = new Group(name:"Football Updates").save()
		def campingSub = new Subscription(name:"Football Updates Subscription", group:campingGroup,
			keyword:"FOOTY", joinAliases:"JOIN,IN,START", leaveAliases:"LEAVE,OUT,STOP",
			defaultAction:"join")
		
		def allrounderBobby = new Contact(name: 'Bobby', mobile: "987654321").save()
		def camperSam = new Contact(name: 'Samson', mobile: "987654322").save()
		def footyRon = new Contact(name: 'Ronaldo', mobile: "987654323").save()

		footballGroup.addToContacts(allrounderBobby)
		footballGroup.addToContacts(footyRon)
		campingGroup.addToContacts(allrounderBobby)
		campingGroup.addToContacts(camperSam)

		footballGroup.save()
		campingGroup.save()
	}

	static createTestMembers(Group g) {
		(1..90).each {
			def c = new Contact(name: "Contact${it}", mobile: "987654321${it}", notes: 'notes').save(failOnError:true, flush:true)
			c.addToGroups(g).save(failOnError:true, flush:true)
		}
	}

	static createTestMessages(Subscription s) {
		(11..90).each {
			def s= Fmessage.build(src:'Bob', text:'Test message number${it}', date:new Date()-it).save(flush:true, failOnError:true)
			s.addToMessages(s) // TODO correct this to sort messages into either join or leave
		}
	}
}