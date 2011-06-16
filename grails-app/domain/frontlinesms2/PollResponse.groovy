package frontlinesms2

class PollResponse extends MessageOwner{
	static transients = ['liveMessageCount']
	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndDeleted(this, false)
		m? m.size(): 0
	}
}
