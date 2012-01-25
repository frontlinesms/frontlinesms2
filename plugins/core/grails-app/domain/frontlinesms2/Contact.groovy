package frontlinesms2

class Contact {
	String name
	String primaryMobile
	String secondaryMobile
	String email
	String notes

	static hasMany = [customFields: CustomField]
	
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

	def beforeInsert = {
		stripNumberFields()
	}
	
	def beforeDelete = {
		GroupMembership.deleteFor(this)
		removeFmessageDisplayName()
	}
	
	def afterInsert = {
		updateFmessageDisplayName()
	}

	def afterUpdate = {
		updateFmessageDisplayName()
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
		def primary = primaryMobile ? Fmessage.countBySrcAndIsDeleted(primaryMobile, false) : 0
		def secondary = secondaryMobile ? Fmessage.countBySrcAndIsDeleted(secondaryMobile, false) : 0
		primary + secondary
	}

	def getOutboundMessagesCount() {
		def primary = primaryMobile ? Dispatch.countByDst(primaryMobile) : 0
		def secondary = secondaryMobile ? Dispatch.countByDst(secondaryMobile) : 0
		primary + secondary
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
	
	def updateFmessageDisplayName() {
		if(primaryMobile) {
			Fmessage.withNewSession { session ->
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE m.src=?", [name, true, primaryMobile])
				Dispatch.findAllByDst(primaryMobile).each {
					it.message.displayName = "To: " + name
					it.message.contactExists = true
				}
			}
		}
	}
	
	private def removeFmessageDisplayName() {
		if(primaryMobile) {
			Fmessage.withNewSession { session ->
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?, m.contactExists=? WHERE m.src=?", [primaryMobile, false, primaryMobile])
				Dispatch.findAllByDst(primaryMobile).each {
					it.message.displayName = "To: " + primaryMobile
					it.message.contactExists = false
				}
			}
		}
	}
		
}
