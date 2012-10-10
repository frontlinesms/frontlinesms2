package frontlinesms2

abstract class Activity extends MessageOwner {
//> STATIC PROPERTIES
	static boolean editable = { true }
	static def implementations = [Announcement, Autoreply, Poll, Subscription, Webconnection]

//> INSTANCE PROPERTIES
	String sentMessageText
	Date dateCreated
	static transients = ['liveMessageCount']

	static hasMany = [keywords: Keyword]

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
		this.messages*.isDeleted = false
	}

	def processKeyword(Fmessage message, Keyword match) {}

	def activate() {}

	def deactivate() {}

	private def logFail(c, ex) {
		ex.printStackTrace()
		log.warn("Error creating routes of webconnection with id $c?.id", ex)
		LogEntry.log("Error creating routes to webconnection with name ${c?.name?: c?.id}")
		//createSystemNotification('connection.route.failNotification', [c.id, c?.name?:c?.id], ex)
	}
}

