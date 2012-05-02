package frontlinesms2

import frontlinesms2.Fmessage

class Trash {
	Date dateCreated
	Long objectId
	Class objectClass
	String displayName
	String displayText
	
	static constraints = {
		displayName(nullable: true)
		displayText(nullable: true)
	}
	
	def getObject() { objectClass.get(objectId) }
}
