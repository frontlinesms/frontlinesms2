package frontlinesms2.contact

import frontlinesms2.*

class GroupBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestGroups() {
		remote {
			Group.build(name:'Listeners')
			Group.build(name:'Friends')
			null
		}
	}

	static createTestContacts() {
		remote {
			def friendsGroup = Group.findByName('Friends')
			def bobby = Contact.build(name:'Bobby').save(failOnError:true, flush:true)
			def duchamps = Contact.build(name:'Duchamps').save(failOnError:true, flush:true)
			[bobby, duchamps].each() { friendsGroup.addToMembers(it) }
			null
		}
	}

	def createTestGroupsAndContacts() {
		createTestGroups()
		createTestContacts()
	}

	static createManyContactsAddToGroups() {
		remote {
			def lastGroupMembership
			(11..90).each {
				def c = Contact.build(name: "Contact${it}", mobile: "987654321${it}", notes: 'notes').save(failOnError:true, flush:true)
				lastGroupMembership = c.addToGroup(Group.findByName('Friends'))
			}
			lastGroupMembership.save(failOnError:true, flush:true)
			null
		}
	}
}

