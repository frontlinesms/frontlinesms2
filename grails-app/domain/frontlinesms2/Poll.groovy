package frontlinesms2

class Poll {
	String title
	String autoReplyText
	String instruction
	String question
	boolean archived
	Date dateCreated

	static hasMany = [responses: PollResponse]
	static fetchMode = [responses: "eager"]

	static constraints = {
		title(unique: true, blank: false, nullable: false, maxSize: 255)
		responses(validator: { val, obj ->
			val?.size() >= 2 &&
					(val*.value as Set)?.size() == val?.size()
		})
		autoReplyText(nullable: true, blank: false)
		instruction(nullable: true)
		question(nullable: true)
	}

	static mapping = {
		responses cascade: 'all'
	}

	def getMessages(params) {
		Fmessage.owned(params['starred'], this.responses).list(params)
	}

	def countMessages(isStarred = false) {
		Fmessage.owned(isStarred, this.responses).count()
	}

	def getResponseStats() {
		def totalMessageCount = countMessages(false)
		responses.sort {it.id}.collect {
			def messageCount = it.liveMessageCount
			[id: it.id,
					value: it.value,
					count: messageCount,
					percent: totalMessageCount ? messageCount * 100 / totalMessageCount as Integer : 0]
		}
	}

	static getNonArchivedPolls() {
		Poll.findAllByArchived(false)
	}
	
	static getArchivedPolls() {
		Poll.findAllByArchived(true)
	}

	static Poll createPoll(attrs) {
		def responses =[]
		if(attrs['poll-type'] == 'standard') {
			['Yes','No'].each { responses << new PollResponse(value:it, key:it)}
		}
		else
		{
			def choices = attrs.findAll{ it ==~ /choice[A-E]=.*/}
			choices.each { k,v -> 
				if(v) 
					responses << new PollResponse(value: v, key:k)
			}
		}

		def unknownResponse = 'Unknown'
		responses << new PollResponse(value: unknownResponse, key: unknownResponse)
		attrs['responses'] = responses
		new Poll(attrs)
	}
}