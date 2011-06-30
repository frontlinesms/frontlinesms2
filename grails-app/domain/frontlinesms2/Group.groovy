package frontlinesms2

class Group {
	String name
	
	static hasMany = [members: Contact]

	static constraints = { name(unique: true, nullable: false, blank: false, maxSize: 255) }
	static mapping = {
	    table 'grup'
	}


	def getAddresses() {
		members*.address
	}

	static def getGroupDetails() {
		def resultMap= [:]
		Group.list().each {resultMap[it.name] = it.members.size()}
		resultMap
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
