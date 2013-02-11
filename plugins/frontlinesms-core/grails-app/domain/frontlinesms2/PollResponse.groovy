package frontlinesms2

class PollResponse implements Comparable {
	String key
	String value
	static belongsTo = [poll: Poll]
	static transients = ['liveMessageCount']

	static mapping = {
		version false
	}
	
	static constraints = {
		value(blank:false, maxSize:255)
	}

	int compareTo(that) {
		key.compareTo(that.key)
	}

	def removeFromMessages(m) {
		if(m.ownerDetail == "$id") m.ownerDetail = null
	}

	boolean isUnknown() {
		return key == Poll.KEY_UNKNOWN
	}
	
	List getMessages() {
		if(poll.messages == null) return []
		if(isUnknown()) {
			return poll.messages.findAll { it.ownerDetail == Poll.KEY_UNKNOWN && it.inbound }.asList()
		}
		return poll.messages.findAll { it.ownerDetail == "$id" && it.inbound }.asList()
	}

	void addToMessages(Fmessage message) {
		if(!message.inbound) return
		if (this.poll.messages == null)
			this.poll.messages = []
		this.poll.messages << message
		message.messageOwner = this.poll
		if(isUnknown()) {
			message.setMessageDetail(this.poll, Poll.KEY_UNKNOWN)
		} else {
			if(!id) throw new IllegalStateException('Cannot add a message to an unsaved PollResponse.')
			message.setMessageDetail(this.poll, "$id")
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

