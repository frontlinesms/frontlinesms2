package frontlinesms2.contact

import frontlinesms2.*

abstract class GroupBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestGroups() {
		remote {
			new Group(name:'Listeners').save(failOnError:true, flush:true)
			new Group(name:'Friends').save(failOnError:true, flush:true)
		}
	}

	def createTestContacts() {
		remote {
			def friendsGroup = Group.findByName('Friends')
			def bobby = new Contact(name:'Bobby').save(failOnError:true, flush:true)
			def duchamps = new Contact(name:'Duchamps').save(failOnError:true, flush:true)
			[bobby, duchamps].each() { friendsGroup.addToMembers(it) }
		}
	}

	def createTestGroupsAndContacts() {
		createTestGroups()
		createTestContacts()
	}

	def createManyContactsAddToGroups() {
		remote {
			def lastGroupMembership
			(11..90).each {
				def c = new Contact(name: "Contact${it}", mobile: "987654321${it}", notes: 'notes').save(failOnError:true, flush:true)
				lastGroupMembership = c.addToGroup(Group.findByName('Friends'))
			}
			lastGroupMembership.save(failOnError:true, flush:true)
		}
	}
}

