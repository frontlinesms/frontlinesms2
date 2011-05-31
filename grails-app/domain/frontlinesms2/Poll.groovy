package frontlinesms2

class Poll {
	String title
	static hasMany = [responses:PollResponse]
	static fetchMode = [responses:"eager"]

	static constraints = {
		title(unique: true, blank: false, nullable: false, maxSize: 255)
		responses(validator: { val, obj ->
				val?.size() >= 2 &&
					(val*.value as Set)?.size() == val?.size()
		})
	}

	static mapping = {
            responses cascade:'all'
	}

	def getMessages() {
		return this.responses*.messages.flatten()
	}

	static Poll createPoll(question, responseList) {
		def r
		boolean unknown = false
		def p = new Poll(title:	question, responses: responseList)
		p.responses.each {
			if(it.value == 'Unknown') {
				unknown = true
			}
		}
		if(unknown == false) {
			r = new PollResponse(value:'Unknown')
			p.addToResponses(r)
		}

		return p
	}
}

