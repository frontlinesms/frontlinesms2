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
}
