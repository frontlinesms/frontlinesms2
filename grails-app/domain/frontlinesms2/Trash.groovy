package frontlinesms2

class Trash {
	Date dateCreated
	Long linkId
	String linkClassName
	String message
	String identifier
	
	def getLink() { getClass().classLoader.loadClass(linkClassName).get(linkId) }
	
	static constraints = {
		message(nullable: true)
		identifier(nullable: true)
	}
}
