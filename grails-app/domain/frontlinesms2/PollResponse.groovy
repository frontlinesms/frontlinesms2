package frontlinesms2

class PollResponse extends MessageOwner {
	static transients = ['liveMessageCount']
	String value
	String key
	static constraints = {
		value(blank: false, nullable: false, maxSize: 255)
		key(nullable: true)
	} // having a unique value here makes all reponses across all polls unique - not what we want right now

	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndDeleted(this, false)
		m? m.size(): 0
	}
}
