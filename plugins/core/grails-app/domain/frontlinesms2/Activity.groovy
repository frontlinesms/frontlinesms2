package frontlinesms2

class Activity extends MessageOwner {
	String name
	String sentMessageText
	Date dateCreated
	static transients = ['liveMessageCount']

	static constraints = {
		name(blank: false, nullable: false, unique: true)
		sentMessageText(nullable:true)
	}
	
	def getActivityMessages(getOnlyStarred=false, getSent=true) {
		Fmessage.owned(this, getOnlyStarred, getSent)
	}
	
	def archive() {
		this.archived = true
		messages*.archived = true
	}
	
	def unarchive() {
		this.archived = false
		Fmessage.owned(this, false, true)?.list()*.each { it?.archived = false }
	}
	
	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndIsDeleted(this, false)
		m ? m.size() : 0
	}

	def processKeyword(Fmessage message, boolean exactMatch) {}
}
