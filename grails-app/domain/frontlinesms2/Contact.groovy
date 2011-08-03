package frontlinesms2

class Contact {
	String name
	String primaryMobile
	String secondaryMobile
	String email
    String notes

	static hasMany = [groups: Group, customFields: CustomField]
	static belongsTo = Group

	def beforeUpdate = {
		updateContactNames("", getOldContactNumber())
		updateContactNames(name, primaryMobile)
	}
	
	def beforeInsert = {
		updateContactNames(name, primaryMobile)
	}
	
	def beforeDelete = {
		updateContactNames(name, "")
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

	boolean isMemberOf(Group group) {
	   groups.contains(group)
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
	
	def updateContactNames(contactName, contactNumber)
	{
		if(contactNumber) {
			 // Prevent stackoverflow exception
			Contact.withNewSession { session -> 
				Fmessage.executeUpdate("update Fmessage m set m.contactName = ? where m.src = ?", [contactName, contactNumber])
			}
		}
	}
	
	private def getOldContactNumber()
	{
		Contact.withNewSession {session ->
			Contact.get(id).refresh().primaryMobile
		}
	}
}