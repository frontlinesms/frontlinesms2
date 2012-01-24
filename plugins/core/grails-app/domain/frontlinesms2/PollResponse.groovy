package frontlinesms2

class PollResponse {
	String value
	String key
	static belongsTo = [poll: Poll]
	static hasMany = [messages: Fmessage]
	static transients = ['liveMessageCount']
	
	static constraints = {
		value(blank: false, nullable: false, maxSize: 255)
		key(nullable: true)
		poll(nullable: false)
		messages(nullable: true)
	}
	
	def addToMessages(message) {
		this.poll.addToMessages(message)
		this.poll.save(flush: true)
		this.addToMessages(message)
	}
	
	def getLiveMessageCount() {
		def m = 0
		this.messages.each {
			if(!it.isDeleted)
				m++
		}
		m
	}
}
