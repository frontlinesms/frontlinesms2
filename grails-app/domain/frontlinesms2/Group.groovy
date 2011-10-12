package frontlinesms2

class Group {
	String name
	String subscriptionKey
	String unsubscriptionKey
	String joinReplyMessage
	String leaveReplyMessage

	static constraints = {
		name(unique: true, nullable: false, blank: false, maxSize: 255)
		subscriptionKey(nullable: true, blank: false, validator: { val, obj ->
			return isUniqueAcrossColumns(val, obj.unsubscriptionKey, obj)
		})
		unsubscriptionKey(nullable: true, blank: false, validator: { val, obj ->
			return isUniqueAcrossColumns(val, obj.subscriptionKey, obj)
		})
		joinReplyMessage(nullable:true, maxSize:255)
		leaveReplyMessage(nullable:true, maxSize:255)
	}

	private static boolean isUniqueAcrossColumns(val, otherVal, obj) {
		return val == null ?:
				((val ==~ /([a-zA-Z0-9]+)/) && (val != otherVal) &&
						Group.findAllBySubscriptionKeyOrUnsubscriptionKey(val, val).every {it.id == obj.id})
	}


	static mapping = {
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
	
	def getAddresses() {
		def addressList = []
		getMembers()*.primaryMobile.each {  
			if(it)	addressList << it
		}
		getMembers()*.secondaryMobile.each {
			if(it)	addressList << it
		}
		addressList
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
	
}
