package frontlinesms2

import frontlinesms2.Fmessage

class Trash {
	Date dateCreated
	Long objectId
	Class objectClass // TODO this could be a Class instead of String.  If keeping as class name, call it that
	String displayDetail
	String displayName
	
	static constraints = {
		displayDetail(nullable: true)
		displayName(nullable: true)
	}
	
	def getObject() { objectClass.get(objectId) } // if this has to be done this way, use Class.forName()

	// TODO would be nice to have a factory method here where database objects can be passed in
	static trashObject(object) {
		if (object instanceof frontlinesms2.Fmessage) {
		
		} else if (object instanceof frontlinesms2.Activity) {
		
		}
	}
}
