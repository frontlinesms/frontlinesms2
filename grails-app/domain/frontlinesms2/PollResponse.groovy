package frontlinesms2

class PollResponse extends MessageOwner{
	static transients = ['liveMessageCount']
	def getLiveMessageCount() {
		def m = Fmessage.findAllByActivityAndDeleted(this, false)
		m? m.size(): 0
	}
}
