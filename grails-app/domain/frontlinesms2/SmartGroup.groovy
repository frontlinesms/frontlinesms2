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
	
	static constraints = {
		contactName(nullable:true, validator:atLeastOneSearchParamValidator)
		mobile(nullable:true, validator:atLeastOneSearchParamValidator)
		email(nullable:true, validator:atLeastOneSearchParamValidator)
		notes(nullable:true, validator:atLeastOneSearchParamValidator)
	}
	
	static def atLeastOneSearchParamValidator = { val, obj ->
		obj.contactName || obj.mobile || obj.email || obj.notes
	}
}
