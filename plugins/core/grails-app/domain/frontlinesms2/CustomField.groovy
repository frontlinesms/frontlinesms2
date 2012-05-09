package frontlinesms2

class CustomField {
	String name
	String value
	static belongsTo = [contact:Contact, smartGroup:SmartGroup]

	static mapping = {
		sort id:'asc'
	}
	
	static constraints = {
		name(blank:false, maxSize:255)
		value(maxSize:255)
		contact(nullable:true)
		smartGroup(nullable:true)
	}

	static def getAllUniquelyNamed() {
		CustomField.createCriteria().list {
			projections {
				distinct 'name'
			}
			order 'name', 'asc'
		}
	}
}
