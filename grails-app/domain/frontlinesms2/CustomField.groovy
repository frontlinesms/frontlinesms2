package frontlinesms2

class CustomField {
	String name
	String value
	static belongsTo = [contact:Contact]
	
    static constraints = {
		name(unique: false, nullable: false, blank: false, maxSize: 255)
		value(unique: false, nullable: true, blank: true, maxSize: 255)
    }

	static def getAllUniquelyNamed() {
		def uniqueNameList = CustomField.createCriteria().list {
			projections {
				distinct('name')
			}
		}
		uniqueNameList
	}
	
	static def getAllContactNameMatchingCustomField(customFields){
		def matchingString = ""
		def conditionString 
		customFields.each{ name, value -> 
			if (value!=null && value!=''){
				conditionString = " WHERE  name='"+name+"' AND lower(value) like lower('%"+value+"%')"
				matchingString = matchingString? (matchingString+" AND cf.contact.name in (SELECT DISTINCT cf2.contact.name FROM CustomField cf2 join cf2.contact"+conditionString+")") : conditionString  
			}
		}
		CustomField.executeQuery("SELECT DISTINCT cf.contact.name FROM CustomField cf join cf.contact"+matchingString)
	}
}
