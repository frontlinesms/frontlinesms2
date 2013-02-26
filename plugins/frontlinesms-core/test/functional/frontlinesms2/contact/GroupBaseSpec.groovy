package frontlinesms2.contact

import frontlinesms2.*

abstract class GroupBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestGroups() {
		new Group(name:'Listeners').save(failOnError:true, flush:true)
		new Group(name:'Friends').save(failOnError:true, flush:true)
	}

	def createTestGroupsAndContacts() {
		def friendsGroup = createTestGroups()
		def bobby = new Contact(name:'Bobby').save(failOnError:true, flush:true)
		def duchamps = new Contact(name:'Duchamps').save(failOnError:true, flush:true)
		[bobby, duchamps].each() { friendsGroup.addToMembers(it) }
	}
	
	def createManyContactsAddToGroups() {
		def lastGroupMembership
		(11..90).each {
			def c = new Contact(name: "Contact${it}", mobile: "987654321${it}", notes: 'notes').save(failOnError:true, flush:true)
			lastGroupMembership = c.addToGroup(Group.findByName('Friends'))
		}
		// TODO this could be changed to `sessionFactory.flush()`, but would need to get hold of the session factory
		lastGroupMembership.save(failOnError:true, flush:true)
	}
}

