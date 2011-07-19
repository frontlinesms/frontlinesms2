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

	def getMessages(isStarred = false, max, offset) {
		Fmessage.owned(isStarred,this.responses).list(sort:"dateReceived", order:"desc", max:max, offset:offset)
	}

	def getMessages(isStarred = false) {
		getMessages(isStarred, null, null)
	}

	def countMessages(isStarred = false) {
		Fmessage.owned(isStarred,this.responses).count()
	}
	
	def getResponseStats() {
		def totalMessageCount = countMessages(false)
		responses.sort{it.id}.collect {
			def messageCount = it.liveMessageCount
			[id: it.id,
					value: it.value,
					count: messageCount,
					percent: totalMessageCount? messageCount * 100 / totalMessageCount as Integer: 0]
		}
	}

	static Poll createPoll(question, responseList) {
		if(!responseList.contains('Unknown')) responseList = responseList << ['Unknown']
		new Poll(title:	question, responses: responseList.flatten().collect{new PollResponse(value:it)})
	}
}
