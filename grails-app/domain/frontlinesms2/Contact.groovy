package frontlinesms2

class Contact {
	String name
	String primaryMobile
	String secondaryMobile
	String email
    String notes

	static hasMany = [customFields: CustomField]

	def beforeUpdate = {
		// FIXME should check if relevant fields are "dirty" here before doing update
		updateContactNames("", getOldContactNumber())
		updateContactNames(name, primaryMobile)
	}
	
	def beforeInsert = {
		updateContactNames(name, primaryMobile)
	}
	
	def beforeDelete = {
		updateContactNames(name, "")
		GroupMembership.deleteFor(this)
	}
	
    static constraints = {
		name(blank: true, maxSize: 255, validator: { val, obj ->
				if(val == '') {
					obj.primaryMobile != ''
					obj.primaryMobile != null
				}
		})
		primaryMobile(unique: true, nullable: true, validator: { val, obj ->
				if(val == '') {
					obj.name != ''
					obj.name != null
				}
		})
		secondaryMobile(unique: false, nullable: true, validator: { val, obj ->
				if(val == '') {
					obj.name != ''
					obj.name != null
				}
				if(val && obj.primaryMobile){
					val != obj.primaryMobile
				}
		})
		email(unique: false, nullable: true, email: true, validator: { val, obj ->
				if(val == '') {
					obj.name != ''
					obj.name != null
				}
		})

        notes(nullable: true, maxSize: 1024)
		customFields(nullable: true, unique: false)
	}

	static mapping = {
		customFields cascade: 'all'
		customFields sort: 'name','value'
	}

	def getGroups() {
		GroupMembership.findAllByContact(this)*.group.sort{it.name}
	}

	def setGroups(groups) {
		this.groups.each() { GroupMembership.remove(this, it) }
		groups.each() { GroupMembership.create(this, it) }
	}

	def addToGroups(Group g, flush=false) {
		GroupMembership.create(this, g, flush)
	}

	def removeFromGroups(Group g, flush=false) {
		GroupMembership.remove(this, g, flush)
	}

	boolean isMemberOf(Group group) {
	   GroupMembership.countByContactAndGroup(this, group) > 0
	}

	def getInboundMessagesCount() {
		def primary = primaryMobile? Fmessage.countByDst(primaryMobile): 0
		def secondary = secondaryMobile? Fmessage.countByDst(secondaryMobile): 0
		def email = email? Fmessage.countByDst(email): 0
		primary + secondary + email
	}

	def getOutboundMessagesCount() {
		def primary = primaryMobile? Fmessage.countBySrc(primaryMobile): 0
		def secondary = secondaryMobile? Fmessage.countBySrc(secondaryMobile): 0
		def email = email? Fmessage.countBySrc(email): 0
		primary + secondary + email
	}
	
	def updateContactNames(contactName, contactNumber) {
		// FIXME this does not take account of secondary phone number - should accept varargs?
		if(contactNumber) {
			 // Prevent stackoverflow exception // FIXME a little more info would be nice
			Contact.withNewSession { session -> 
				Fmessage.executeUpdate("update Fmessage m set m.contactName = ? where m.src = ?", [contactName, contactNumber])
			}
		}
	}
	
	private def getOldContactNumber() {
		Contact.withNewSession {session ->
			Contact.get(id).refresh().primaryMobile // FIXME why not use this.loadedState?
		}
	}
}
