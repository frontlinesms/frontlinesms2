package frontlinesms2

class Group {
	String name

	static constraints = { name(unique: true, nullable: false, blank: false, maxSize: 255) }
	static mapping = {
	    members cascade:'save-update'
	    table 'grup'
	}

	def beforeDelete = {
		GroupMembership.deleteFor(this)
	}

	def getMembers() {
		GroupMembership.findAllByGroup(this)*.contact.sort{it.name}
	}

	def addToMembers(Contact c) {
		GroupMembership.create(c, this)
	}

	static Set<Contact> findAllWithoutMember(Contact c) {
		// FIXME do this with a single select/join??
		def allGroups = Group.findAll();
		def cGroups = c.groups
		def without = allGroups
		cGroups.each() { cg ->
			def remove
			allGroups.each() { ag ->
				if(ag.id == cg.id) remove = ag
			}
			if(remove) {
				allGroups.remove(remove)
			}
		}
		without as Set
	}
}
