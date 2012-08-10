package frontlinesms2

abstract class Activity extends MessageOwner {
//> STATIC PROPERTIES
	static boolean editable = { true }

//> INSTANCE PROPERTIES
	String sentMessageText
	Date dateCreated
	static transients = ['liveMessageCount']

	static mapping = {
		tablePerHierarchy false
		version false
	}

	static constraints = {
		sentMessageText(nullable:true)
	}

//> ACCESSORS
	def getActivityMessages(getOnlyStarred=false, getSent=true) {
		Fmessage.owned(this, getOnlyStarred, getSent)
	}
	
	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndIsDeleted(this, false)
		m ? m.size() : 0
	}

//> ACTIONS
	def archive() {
		this.archived = true
		messages.each {
			it.archived = true
		}
	}
	
	def unarchive() {
		this.archived = false
		messages.each { it.archived = false }
	}

	def restoreFromTrash() {
		this.deleted = false
		activity.messages*.isDeleted = false
	}

	def processKeyword(Fmessage message, boolean exactMatch) {}
}

