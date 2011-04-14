package frontlinesms2

abstract class GroupGebSpec extends grails.plugin.geb.GebSpec {
	def createTestGroups() {
		new Group(name: 'Listeners').save()
		new Group(name: 'Friends').save()
	}

	def createTestGroupsAndContacts() {
		def friendsGroup = createTestGroups()
		def bobby = new Contact(name: 'Bobby').save()
		def duchamps = new Contact(name: 'Duchamps').save()
		[bobby, duchamps].each() { friendsGroup.addToMembers(it) }
		friendsGroup.save(failOnError: true, flush: true)
	}
}

