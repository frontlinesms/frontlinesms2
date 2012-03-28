package frontlinesms2

class Contact {
	String name
	String primaryMobile
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
		if(primaryMobile) {
			Fmessage.withNewSession { session ->
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE m.src=?", [name, true, primaryMobile])
				updateDispatchInfo()
			}
		}
	}
	
	def beforeUpdate = {
		final def oldMobile = isDirty('primaryMobile')? getPersistentValue('primaryMobile'): null
		if(oldMobile) {
			Fmessage.withNewSession { session ->
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=m.src,m.contactExists=? WHERE m.src=?", [false, oldMobile])
				updateDispatchInfo()
			}
		}
	}

	def afterUpdate = {
		println "afterUpdate() : ENTRY : primaryMobile=$primaryMobile"
		println "afterUpdate() : primaryMobile.dirty=${isDirty('primaryMobile')}"
		if(primaryMobile) {
			println "afterUpdate() : creating new session..."
			Fmessage.withNewSession { session ->
				println "afterUpdate() : inside new session..."
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE m.src=?", [name, true, primaryMobile])
				updateDispatchInfo()
			}
		}
		println "afterUpdate() : EXIT"
	}
	
	private def updateDispatchInfo() {
		if(primaryMobile) {
			Dispatch.findAllByDst(primaryMobile).each {
				it.message.displayName = "To: " + name
				it.message.contactExists = true
			}
		}
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

	def removeFromGroup(Group g, flush=false) {
		removeFromGroups(g, flush)
	}

	def removeFromGroups(Group g, flush=false) { // FIXME why is this method name plural when only one group is added?
		GroupMembership.remove(this, g, flush)
	}

	boolean isMemberOf(Group group) {
	   GroupMembership.countByContactAndGroup(this, group) > 0
	}

	def getInboundMessagesCount() {
		primaryMobile ? Fmessage.countBySrcAndIsDeleted(primaryMobile, false) : 0
	}

	def getOutboundMessagesCount() {
		primaryMobile? Dispatch.messageCount(this).count(): 0
	}
	
	def stripNumberFields() {
		def n = primaryMobile?.replaceAll(/\D/, '')
		if(primaryMobile && primaryMobile[0] == '+') n = '+' + n
		primaryMobile = n
	}
	
	private def removeFmessageDisplayName() {
		if(primaryMobile) {
			Fmessage.withNewSession { session ->
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?, m.contactExists=? WHERE m.src=?", [primaryMobile, false, primaryMobile])
				updateDispatchInfo()
			}
		}
	}
}
