package frontlinesms2

class CustomField {
	String name
	String value
	static belongsTo = [contact:Contact, smartGroup:SmartGroup]
	
	static constraints = {
		name(unique: false, nullable: false, blank: false, maxSize: 255)
		value(unique: false, nullable: true, blank: true, maxSize: 255)
		contact(nullable:true)
		smartGroup(nullable:true)
	}

	static def getAllUniquelyNamed() {
		CustomField.createCriteria().list {
			projections {
				distinct('name')
			}
		}
	}
	
	static def getAllContactsWithCustomField(customFields) {
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
