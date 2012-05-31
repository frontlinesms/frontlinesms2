package frontlinesms2

import frontlinesms2.Fmessage

class Trash {
	Date dateCreated
	Long objectId
	String objectClass
	String displayName
	String displayText
	
	static constraints = {
		displayName(nullable: true)
		displayText(nullable: true)
	}

	static def findByObject(def o) {
		findByObjectIdAndObjectClass(o.id, o.getClass().name)
	}
	
	def getObject() {
		// N.B. Class.forName will not work as expected here
		Thread.currentThread().contextClassLoader.loadClass(objectClass).get(objectId)
	}
}

