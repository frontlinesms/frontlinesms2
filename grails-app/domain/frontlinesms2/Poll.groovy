package frontlinesms2

class Poll {
	String title
	static hasMany = [responses:PollResponse]
	static fetchMode = [responses:"eager"]

	static constraints = {
		title(unique: true, blank: false, nullable: false, maxSize: 255)
		responses(unique: true, validator: { val, obj ->
				val?.size() >= 2
		})
	}

	static mapping = {
            responses cascade:'all'
	}

	def getMessages() {
		return this.responses*.messages.flatten()
	}
}

