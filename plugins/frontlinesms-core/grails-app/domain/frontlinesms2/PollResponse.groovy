package frontlinesms2

class PollResponse {
	String key
	String value
	static belongsTo = [poll: Poll]
	static transients = ['liveMessageCount']

	static mapping = {
		version false
	}
	
	static constraints = {
		value(blank:false, nullable:false, maxSize:255)
		poll(nullable:false)
		key(nullable:true)
	}

	List getMessages() {
		if(poll.messages == null) return []
		if(isUnknown()) {
			return poll.messages.findAll { !it.ownerDetail }.asList()
		}
		return poll.messages.findAll { it.ownerDetail == "$id" }.asList()
	}

	def removeFromMessages(m) {
		if(m.ownerDetail == "$id") m.ownerDetail = null
	}

	boolean isUnknown() {
		return key == Poll.KEY_UNKNOWN
	}
	
	void addToMessages(Fmessage message) {
		if(!message.inbound) return
		if (this.poll.messages == null)
			this.poll.messages = []
		this.poll.messages << message
		message.messageOwner = this.poll
		if(isUnknown()) {
			message.ownerDetail = null
		} else {
			if(!id) throw new IllegalStateException('Cannot add a message to an unsaved PollResponse.')
			message.ownerDetail = "$id"
		}
		message.save()
	}
	
	def getLiveMessageCount() {
		messages.count { it.inbound && !it.isDeleted && (it.archived == poll.archived) }
	}

//> FACTORY METHODS
	static PollResponse createUnknown() {
		new PollResponse(value:'Unknown', key:Poll.KEY_UNKNOWN)
	}
}
