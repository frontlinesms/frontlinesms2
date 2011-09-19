package frontlinesms2

class Poll {
	String title
	String keyword
	String autoReplyText
	String question
	boolean archived
	Date dateCreated
	List responses

	static hasMany = [responses: PollResponse]

	static constraints = {
		title(blank: false, nullable: false, maxSize: 255, validator: { title, me ->
			def matching = Poll.findByTitleIlike(title)
			matching==null || matching==me
		})
		responses(validator: { val, obj ->
			val?.size() >= 2 &&
					(val*.value as Set)?.size() == val?.size()
		})
		autoReplyText(nullable:true, blank:false)
		question(nullable:true)
		keyword(nullable:true, validator: { keyword, me ->
			if(!keyword) return true
			else {
				if(keyword.find(/\s/)) return false
				else {
					if(me.archived) return true
					else {
						def matching = Poll.findByArchivedAndKeyword(false, keyword.toUpperCase())
						return matching == null || matching.id == me.id
					}
				}
			}
		})
	}
	
	def beforeSave = {
		keyword = (!keyword?.trim())? null: keyword.toUpperCase()
	}
	def beforeUpdate = beforeSave
	def beforeInsert = beforeSave

	def getMessages(params=[:]) {
		Fmessage.owned(params.starred, this.responses).list(params)
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
	
	def archivePoll() {
		this.archived = true
		def messagesToArchive = Fmessage.owned(this.responses).list()
		messagesToArchive.each { it.archived = true }
	}

	static getNonArchivedPolls() {
		Poll.findAllByArchived(false)
	}
	
	static getArchivedPolls() {
		Poll.findAllByArchived(true)
	}

	static Poll createPoll(attrs) {
		def poll = new Poll(attrs)
		if(attrs['poll-type'] == 'standard') {
			['Yes','No'].each { poll.addToResponses(new PollResponse(value:it, key:it)) }
		} else {
			def choices = attrs.findAll{ it ==~ /choice[A-E]=.*/}
			choices.each { k,v -> 
				if(v) poll.addToResponses(new PollResponse(value: v, key:k))
			}
		}
		poll.addToResponses(new PollResponse(value: 'Unknown', key: 'Unknown'))
		poll
	}
}