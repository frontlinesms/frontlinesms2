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

	
	static findByCustomFields(fields) {
		def matches = []
		if (fields == [:])
			matches = Contact.getAll()
		else
			fields.each { field ->
				def list = Contact.byCustomFieldNameandValue(field).list()
				if (matches == [])
					list.each { matches << it}
				else
					matches.retainAll(list)
			}
		return matches
	}
	
	static namedQueries = {
		byCustomFieldNameandValue { field ->
			customFields {
				eq 'name', field.key
				ilike 'value', "%${field.value}%"
			}
		}
	}
}

