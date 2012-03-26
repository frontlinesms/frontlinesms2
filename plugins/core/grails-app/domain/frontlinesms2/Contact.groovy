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
		if(primaryMobile || secondaryMobile) {
			Fmessage.withNewSession { session ->
				def clauses = []
				def variables = [name, true]
				if(primaryMobile) {
					clauses << 'm.src=?'
					variables << primaryMobile
				}
				if(secondaryMobile) {
					clauses << 'm.src=?'
					variables << secondaryMobile
				}
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE " + clauses.join(' OR '), variables)
				updateDispatchInfo()
			}
		}
	}
	
	def beforeUpdate = {
		final def old1 = isDirty('primaryMobile')? getPersistentValue('primaryMobile'): null
		final def old2 = isDirty('secondaryMobile')? getPersistentValue('secondaryMobile'): null
		if(old1 || old2) {
			Fmessage.withNewSession { session ->
		println "beforeUpdate() : primaryMobile.dirty=${isDirty('primaryMobile')}; secondaryMobile.dirty=${isDirty('secondaryMobile')}"
		println "beforeUpdate() : getPersistentValue('primaryMobile'):${getPersistentValue('primaryMobile')}"
		println "beforeUpdate() : getPersistentValue('secondaryMobile'):${getPersistentValue('secondaryMobile')}"
				def clauses = []
				def variables = [false]
				if(old1) {
					println "appending primaryMobile to varialbes"
					clauses << 'm.src=?'
					variables << old1
				}
				if(old2) {
					println "appending secondaryMobile to varialbes"
					clauses << 'm.src=?'
					variables << old2
				}
				println "Variables: $variables; clauses: $clauses"
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=m.src,m.contactExists=? WHERE " + clauses.join(' OR '), variables)
				updateDispatchInfo()
			}
		}
		println "beforeUpdate() : EXIT"
	}

	def afterUpdate = {
		println "afterUpdate() : ENTRY (primaryMobile=$primaryMobile; secondaryMobile=$secondaryMobile)"
		println "afterUpdate() : primaryMobile.dirty=${isDirty('primaryMobile')}; secondaryMobile.dirty=${isDirty('secondaryMobile')}"
		if(primaryMobile || secondaryMobile) {
			println "afterUpdate() : creating new session..."
			Fmessage.withNewSession { session ->
				println "afterUpdate() : inside new session..."
				def clauses = []
				def variables = [name, true]
				if(primaryMobile) {
					clauses << 'm.src=?'
					variables << primaryMobile
				}
				if(secondaryMobile) {
					clauses << 'm.src=?'
					variables << secondaryMobile
				}
				Fmessage.executeUpdate("UPDATE Fmessage m SET m.displayName=?,m.contactExists=? WHERE " + clauses.join(' OR '), variables)
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
		def primary = primaryMobile ? Fmessage.countBySrcAndIsDeleted(primaryMobile, false) : 0
		def secondary = secondaryMobile ? Fmessage.countBySrcAndIsDeleted(secondaryMobile, false) : 0
		primary + secondary
	}

	def getOutboundMessagesCount() {
		def count = 0
		if(primaryMobile || secondaryMobile) {
			count = Dispatch.messageCount(this).count()
		}
		count
	}
	
	def stripNumberFields() {
		def n = primaryMobile?.replaceAll(/\D/, '')
		if(primaryMobile && primaryMobile[0] == '+') n = '+' + n
		primaryMobile = n
		def s = secondaryMobile?.replaceAll(/\D/, '')
		if(secondaryMobile && secondaryMobile[0] == '+') s = '+' + s
		secondaryMobile = s
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
