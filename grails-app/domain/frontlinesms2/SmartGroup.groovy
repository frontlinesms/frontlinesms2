package frontlinesms2

class SmartGroup {
//> SMART GROUP PROPERTIES
	/** the name of this smart group itself.  This is mandatory. */
	String name
	
//> SEARCH PARAMS
	String contactName
	String mobile
	String email
	String notes
	
	static hasMany = [customFields: CustomField]
	
	static constraints = {
		contactName(nullable:true, validator:atLeastOneSearchParamValidator)
		mobile(nullable:true, validator:atLeastOneSearchParamValidator)
		email(nullable:true, validator:atLeastOneSearchParamValidator)
		notes(nullable:true, validator:atLeastOneSearchParamValidator)
	}
	
	static def atLeastOneSearchParamValidator = { val, obj ->
		obj.contactName || obj.mobile || obj.email || obj.notes || (obj.customFields && obj.customFields.each { it.value })
	}
	
	def getMembers() {
		getMembersByName(null, [:])
	}
	
	def getMembersByName(String searchString, Map pageParams) {
		def query = getMembersByNameQuery(searchString)
		println """Executing query:
:::::::$query.where
params:$query.params"""
		Contact.findAll(query.where, query.params)
	}
	
	def countMembersByName(String searchString) {
		def query = getMembersByNameQuery(searchString)
		Contact.executeQuery("SELECT COUNT(c) $query.where", query.params)[0]
	}
	
	private def getMembersByNameQuery(String searchString) {
		def w = []
		def p = [:]
		
		if(searchString) {
			w << "lower(c.name) LIKE lower(:nameSubSearch)"
			p.nameSubSearch = "%$searchString%"
		}
		
		if(contactName) {
			w << "lower(c.name) LIKE lower(:contactName)"
			p.contactName = "%$contactName%"
		}
		
		if(mobile) {
			w << "(c.primaryMobile LIKE :mobile OR c.secondaryMobile LIKE :mobile)"
			p.mobile = "$mobile%"
		}
		
		if(email) {
			w << "lower(c.email) LIKE lower(:email)"
			p.email = "%$email%"
		}
		
		if(notes) {
			w << "lower(c.notes) LIKE lower(:notes)"
			p.notes = "%$notes%"
		}
		
		if(customFields) {
			customFields.each {
				// FIXME potential for injection via it.name?
				w << "c IN (SELECT DISTINCT cf.contact FROM CustomField AS cf WHERE \
cf.name=:custom_${it.name}_name AND LOWER(cf.value) LIKE LOWER(:custom_${it.name}_value))"
				p."custom_${it.name}_name" = it.name
				p."custom_${it.name}_value" = "%$it.value%"
			}
		}
		
		def where = w.join(' AND ')
		return [where:"FROM Contact AS c WHERE $where", params:p]
	}

	static def getMembersByNameIlike(id, String searchString, Map pageParams) {
		SmartGroup.get(id).getMembersByName(searchString, pageParams)
	}

	static def countMembersByNameIlike(id, String searchString) {
		SmartGroup.get(id).countMembersByName(searchString)
	}
}
