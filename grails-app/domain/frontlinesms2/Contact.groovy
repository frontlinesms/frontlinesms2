package frontlinesms2

class Contact {
	String name
	String address
    String notes

	static hasMany = [groups: Group, customFields: CustomField]
	static belongsTo = Group 

    static constraints = {
		name(blank: true, maxSize: 255, validator: { val, obj ->
				if(val == '') {
					obj.address != ''
					obj.address != null
				}
		})
		address(unique: true, nullable: true, validator: { val, obj ->
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
		address? Fmessage.countByDst(address): 0
	}

	def getOutboundMessagesCount() {
		address? Fmessage.countBySrc(address): 0
	}
}
