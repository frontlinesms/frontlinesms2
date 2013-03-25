package frontlinesms2

abstract class Activity extends MessageOwner {
//> STATIC PROPERTIES
	static boolean editable = { true }
	static def implementations = [Announcement, Autoreply, Poll, Subscription, Webconnection, Autoforward, CustomActivity]
	protected static final def NAME_VALIDATOR = { activityDomainClass ->
		return { val, obj ->
			if(obj?.deleted || obj?.archived) return true
			def identical = activityDomainClass.findAllByNameIlike(val)
			if(!identical) return true
			else if (identical.any { it.id != obj.id && !it?.archived && !it?.deleted }) return false
			else return true
		}
	}

//> INSTANCE PROPERTIES
	String sentMessageText
	Date dateCreated
	static transients = ['liveMessageCount']

	static hasMany = [keywords: Keyword]
	List keywords

	static mapping = {
		tablePerHierarchy false
		version false
		keywords cascade: "all-delete-orphan"
	}

	static constraints = {
		sentMessageText(nullable:true)
		keywords(validator: { val, obj ->
			!val || val?.every { it.validate() }
		})
	}

//> ACCESSORS
	def getActivityMessages(getOnlyStarred=false, getSent=null, stepId=null, params=null) {
		Fmessage.owned(this, getOnlyStarred, getSent).list(params?:[:])
	}

	def getMessageCount(getOnlyStarred=false, getSent=null) {
		Fmessage.owned(this, getOnlyStarred, getSent).count()
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

	def processKeyword(Fmessage message, Keyword match) {
		message.setMessageDetail(this, "")
		this.addToMessages(message)
		this.save(failOnError:true)
	}

	/**
	 * Activcate this activity.  If it is already activated, this method should
	 * deactivate it and then reactivate it.
	 */
	def activate() {}

	def deactivate() {}

	def move(messageInstance) {
		messageInstance.messageOwner?.removeFromMessages(messageInstance)?.save(failOnError:true)
		this.processKeyword(messageInstance, null)
	}
}

