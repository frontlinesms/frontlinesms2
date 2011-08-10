package frontlinesms2

class Group {
	String name
	String subscriptionKey
	String unsubscriptionKey

	static hasMany = [members: Contact]

	static constraints = {
		name(unique: true, nullable: false, blank: false, maxSize: 255)
		subscriptionKey(nullable: true, blank: false, validator: { val, obj ->
			return isUniqueAcrossColumns(val, obj.unsubscriptionKey, obj)
		})
		unsubscriptionKey(nullable: true, blank: false, validator: { val, obj ->
			return isUniqueAcrossColumns(val, obj.subscriptionKey, obj)
		})
	}

	private static boolean isUniqueAcrossColumns(val, otherVal, obj) {
		return val == null ?:
				((val ==~ /([a-zA-Z0-9]+)/) && (val != otherVal) &&
						Group.findAllBySubscriptionKeyOrUnsubscriptionKey(val, val).every {it.id == obj.id})
	}


	static mapping = {
	    table 'grup'
	}


	def getAddresses() {
		members*.primaryMobile
	}

	static HashMap<String, List<String>> getGroupDetails() {
		def resultMap= [:]
		Group.list().each {resultMap[it.name] = it.getAddresses()}
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
	
	static removeContactFromGroups(Contact contact) {
		def contactGroups = contact.groups
		contactGroups.each() { cg ->
			cg.getMembers().remove(contact)
		}
	}
}
