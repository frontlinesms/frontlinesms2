package frontlinesms2

class Contact {
	String name
	String primaryMobile
	String secondaryMobile
	String email
	String notes

	static hasMany = [customFields: CustomField]
	
	def beforeInsert = {
		stripNumberFields()
	}
	
	def beforeDelete = {
		GroupMembership.deleteFor(this)
		removeFmessageContacts()
	}
	
	def afterInsert = {
		updateFmessageContacts()
	}

	def afterUpdate = {
		updateFmessageContacts()
	}
	
	static constraints = {
		name(blank: true, maxSize: 255, validator: { val, obj ->
			val || obj.primaryMobile
		})
		primaryMobile(unique: true, nullable: true, validator: { val, obj ->
			val || obj.name
		})
		secondaryMobile(unique: false, nullable: true, validator: { val, obj ->
			!(val && val==obj.primaryMobile)
		})
		email(unique:false, nullable:true, email:true)
		notes(nullable:true, maxSize:1024)
		customFields(nullable: true, unique: false)
	}

	static mapping = {
		sort: 'name'
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

	def addToGroup(Group g, flush=false) {
		addToGroups(g, flush)
	}

	def addToGroups(Group g, flush=false) { // FIXME why is this method name plural when only one group is added?
		GroupMembership.create(this, g, flush)
	}

	def removeFromGroup(Group g, flush=false) { // FIXME why is this method name plural when only one group is added?
		removeFromGroups(g, flush)
	}

	def removeFromGroups(Group g, flush=false) { // FIXME why is this method name plural when only one group is added?
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
	
	def stripNumberFields() {
		def n = primaryMobile?.replaceAll(/\D/, '')
		if(primaryMobile && primaryMobile[0] == '+') n = '+' + n
		primaryMobile = n
		def s = secondaryMobile?.replaceAll(/\D/, '')
		if(secondaryMobile && secondaryMobile[0] == '+') s = '+' + s
		secondaryMobile = s
	}
	
	private def getOldContactNumber() {
		Contact.withNewSession {session ->
			Contact.get(id).refresh().primaryMobile // FIXME why not use this.loadedState?
		}
	}
	
	def updateFmessageContacts() {
		Fmessage.withNewSession { session ->
			Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE m.src=?", [name, true, primaryMobile])
			Dispatch.findAllByDst(primaryMobile).each {
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE m.dispatches=?", [name, true, it])
			}
		}
	}
	
	private def removeFmessageContacts() {
		Fmessage.withNewSession { session ->
			Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE m.src=?", ['', false, primaryMobile])
			Dispatch.findAllByDst(primaryMobile).each {
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE m.dispatches=?", ['', false, it])
			}
		}
	}
		
}
