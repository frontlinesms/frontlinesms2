package frontlinesms2.contact

import frontlinesms2.*

abstract class GroupBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestGroups() {
		new Group(name: 'Listeners').save()
		new Group(name: 'Friends').save(failOnError: true, flush: true)
	}

	def createTestGroupsAndContacts() {
		def friendsGroup = createTestGroups()
		def bobby = new Contact(name: 'Bobby').save()
		def duchamps = new Contact(name: 'Duchamps').save()
		[bobby, duchamps].each() { friendsGroup.addToMembers(it) }
		friendsGroup.save(failOnError: true, flush: true)
	}
	
	def createManyContactsAddToGroups() {
		(11..90).each {
			def c = new Contact(name: "Contact${it}", primaryMobile: "987654321${it}", notes: 'notes').save(failOnError:true, flush:true)
			c.addToGroups(Group.findByName('Friends')).save(failOnError:true, flush:true)
		}
	}
	
}

