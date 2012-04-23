package frontlinesms2

class Trash {
	Date dateCreated
	Long linkId // TODO rename this objectId
	String objectType // TODO this could be a Class instead of String.  If keeping as class name, call it that
	String message // TODO rename this something like displayDetail
	String identifier // TODO rename this displayName as that is what it is
	
	def getLink() { getClass().classLoader.loadClass(objectType).get(linkId) } // if this has to be done this way, use Class.forName()
	
	static constraints = {
		message(nullable: true)
		identifier(nullable: true)
	}

	// TODO would be nice to have a factory method here where database objects can be passed in
}
