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
		}
	}
	
	static def getAllContactsWithCustomField(customFields) {
		// TODO should be able to replace this with criteria and projections
		def matchingString = ''
		customFields.each { name, value -> 
			// FIXME this query should use named variables instead of inserting values directly into the HQL
			def conditionString = "WHERE name='$name' AND LOWER(value) LIKE LOWER('%$value%')"
			matchingString = matchingString ? "$matchingString AND cf.contact IN (SELECT DISTINCT cf2.contact FROM CustomField cf2 JOIN cf2.contact $conditionString)":
		 	conditionString
		}	
		// FIXME this query should use named variables instead of inserting values directly into the HQL
		CustomField.executeQuery("SELECT DISTINCT cf.contact FROM CustomField cf JOIN cf.contact $matchingString")
	}
}
