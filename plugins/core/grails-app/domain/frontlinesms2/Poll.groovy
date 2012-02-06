package frontlinesms2

class Poll extends Activity {
	String keyword
	String autoReplyText
	String question
	List responses

	static hasMany = [responses: PollResponse]
	
	static constraints = {
		name(blank: false, nullable: false, maxSize: 255, validator: { name, me ->
			def matching = Poll.findByNameIlike(name)
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
	
	void addToMessages(Fmessage message) {
		message.messageOwner = this
		if(message.inbound) {
			this.responses.each {
				it.removeFromMessages(message)
			}
			this.responses.find { it.value == 'Unknown' }.messages.add(message)
		}
	}
	void removeFromMessages(Fmessage message) {
		this.messages.remove(this)
		if(message.inbound) {
			this.responses.each {
				it.removeFromMessages(message)
			}
		}
	}
	
	def beforeSave = {
		keyword = (!keyword?.trim())? null: keyword.toUpperCase()
	}
	
	def beforeUpdate = beforeSave
	def beforeInsert = beforeSave

	def getResponseStats() {
		def totalMessageCount = getActivityMessages(false, false).count()
		responses.sort {it.key?.toLowerCase()}.collect {
			def messageCount = it.liveMessageCount
			[id: it.id,
					value: it.value,
					count: messageCount,
					percent: totalMessageCount ? messageCount * 100 / totalMessageCount as Integer : 0]
		}
	}
	
	static Poll createPoll(attrs) {
		def poll = new Poll(attrs)
		if(attrs['poll-type'] == 'standard') {
			poll.addToResponses(new PollResponse(value:'Yes', key:'A'))
			poll.addToResponses(new PollResponse(value:'No', key:'B'))
		} else {
			def choices = attrs.findAll{ it ==~ /choice[A-E]=.*/}
			choices.each { k,v -> 
				if(v) poll.addToResponses(new PollResponse(value: v, key:k))
			}
		}
		poll.addToResponses(new PollResponse(value: 'Unknown', key: 'Unknown'))
		poll.save(flush: true, failOnError: true)
	}
	
	def getType() {
		return 'poll'
	}
}
