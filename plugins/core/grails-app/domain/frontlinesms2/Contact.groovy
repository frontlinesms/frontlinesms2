package frontlinesms2

class Contact {
//> PROPERTIES
	String name
	String mobile
	String email
	String notes

	static hasMany = [customFields: CustomField]
	
	static constraints = {
		name(blank: true, maxSize: 255, validator: { val, obj ->
			val || obj.mobile
		})
		mobile(unique: true, nullable: true, validator: { val, obj ->
			val || obj.name
		})
		email(unique:false, nullable:true, email:true)
		notes(nullable:true, maxSize:1024)
		customFields(nullable: true, unique: false)
	}

	static mapping = {
		sort name:'asc'
		customFields cascade: 'all'
		customFields sort: 'name','value'
	}

//> EVENT METHODS
	def beforeInsert = {
		stripNumberFields()
	}
	
	def beforeDelete = {
		GroupMembership.deleteFor(this)
		removeFmessageDisplayName()
	}
	
	def afterInsert = {
		if(mobile) {
			Fmessage.withNewSession { session ->
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE m.src=?", [name, true, mobile])
				updateDispatchInfo()
			}
		}
	}
	
	def beforeUpdate = {
		final def oldMobile = isDirty('mobile')? getPersistentValue('mobile'): null
		if(oldMobile) {
			Fmessage.withNewSession { session ->
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=m.src,m.contactExists=? WHERE m.src=?", [false, oldMobile])
				updateDispatchInfo()
			}
		}
	}

	def afterUpdate = {
		println "afterUpdate() : ENTRY : mobile=$mobile"
		println "afterUpdate() : mobile.dirty=${isDirty('mobile')}"
		if(mobile) {
			println "afterUpdate() : creating new session..."
			Fmessage.withNewSession { session ->
				println "afterUpdate() : inside new session..."
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE m.src=?", [name, true, mobile])
				updateDispatchInfo()
			}
		}
		println "afterUpdate() : EXIT"
	}
	
	private def updateDispatchInfo() {
		if(mobile) {
			Dispatch.findAllByDst(mobile).each {
				it.message.displayName = "To: " + name
				it.message.contactExists = true
			}
		}
	}
	
//> ACCESSORS
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
		mobile ? Fmessage.countBySrcAndIsDeleted(mobile, false) : 0
	}

	def getOutboundMessagesCount() {
		mobile? Dispatch.messageCount(this).count(): 0
	}
	
	def stripNumberFields() {
		def n = mobile?.replaceAll(/\D/, '')
		if(mobile && mobile[0] == '+') n = '+' + n
		mobile = n
	}

	static namedQueries = {
		findAllWithCustomFields { fields ->
			fields.each { field ->
				customFields {
					eq('name', field.key)
					ilike('value', "%$field.value%")
				}
			}
		}
	}
	
//> HELPER METHODS
	private def removeFmessageDisplayName() {
		if(mobile) {
			Fmessage.withNewSession { session ->
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?, m.contactExists=? WHERE m.src=?", [mobile, false, mobile])
				updateDispatchInfo()
			}
		}
	}
}
