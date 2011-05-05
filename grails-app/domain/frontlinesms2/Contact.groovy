package frontlinesms2

class Contact {
	String name
	String address

	static constraints = {
		name(blank: false)
		address(unique: true, nullable: true)
	}

	def beforeDelete = {
		GroupMembership.deleteFor(this)
	}

	Set<Group> getGroups() {
  		GroupMembership.findAllByContact(this).collect { it.group } as Set
	}

	def addToGroups(Group g) {
		GroupMembership.create(this, g)
	}

	boolean isMemberOf(Group group) {
	   GroupMembership.countByContactAndGroup(this, group) > 0
	}
}
