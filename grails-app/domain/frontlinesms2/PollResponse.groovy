package frontlinesms2

class PollResponse extends MessageOwner {
	static transients = ['liveMessageCount']
	String value
	String key
	static belongsTo = [poll:Poll]
	
	static constraints = {
		value(blank: false, nullable: false, maxSize: 255)
		key(nullable: true)
		poll(nullable: false)
	}

	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndDeleted(this, false)
		m ? m.size() : 0
	}
}
