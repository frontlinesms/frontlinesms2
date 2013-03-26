package frontlinesms2

class Trash {
	Date dateCreated
	Long objectId
	String objectClass
	String displayName
	String displayText

	static final int MAXIMUM_DISPLAY_TEXT_SIZE = 255
	
	static constraints = {
		objectId unique:'objectClass'
		displayName(nullable: true)
		displayText(nullable: true, size: 0..MAXIMUM_DISPLAY_TEXT_SIZE)
	}

	static def findByObject(def o) {
		findByObjectIdAndObjectClass(o.id, o.getClass().name)
	}
	
	def getObject() {
		// N.B. Class.forName will not work as expected here
		Thread.currentThread().contextClassLoader.loadClass(objectClass).get(objectId)
	}
}

