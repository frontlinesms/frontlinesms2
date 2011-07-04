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

	def getMessages(isStarred = false) {
		Fmessage.createCriteria().list {
			and {
				eq("deleted", false)
				'in'("messageOwner", this.responses)
				if(isStarred)
					eq("starred", true)
			}
			order('dateReceived', 'desc')
		}
	}

	def getResponseStats() {
		def totalMessageCount = messages.size()
		responses.sort{it.id}.collect {
			def messageCount = it.liveMessageCount
			[id: it.id,
					value: it.value,
					count: messageCount,
					percent: totalMessageCount? messageCount * 100 / totalMessageCount as Integer: 0]
		}
	}

	static Poll createPoll(question, responseList) {
		if(!responseList.contains('Unknown')) responseList = ['Unknown'] << responseList
		new Poll(title:	question, responses: responseList.flatten().collect{new PollResponse(value:it)})
	}
}
