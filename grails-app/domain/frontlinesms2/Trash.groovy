package frontlinesms2

class Trash {
	Date dateCreated
	Long linkId
	String objectType
	String message
	String identifier
	
	def getLink() { getClass().classLoader.loadClass(objectType).get(linkId) }
	
	static constraints = {
		message(nullable: true)
		identifier(nullable: true)
	}
}
