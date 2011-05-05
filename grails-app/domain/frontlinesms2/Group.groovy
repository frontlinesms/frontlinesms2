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

    Set<Contact> getMembers() {
  		GroupMembership.findAllByGroup(this).collect { it.contact } as Set
    }

	def addToMembers(Contact c) {
		GroupMembership.create(c, this)
	}
}
