package frontlinesms2

class Poll extends Activity {
	static hasOne = [keyword: Keyword]
	String autoreplyText
	String question
	List responses

	static hasMany = [responses: PollResponse]
	
	static mapping = {
        keyword cascade: 'all'
    }
			
	def getType() {
		return 'poll'
	}
	
	static constraints = {
		name(blank: false, nullable: false, maxSize: 255, unique: true)
		responses(validator: { val, obj ->
			val?.size() > 2 &&
					(val*.value as Set)?.size() == val?.size()
		})
		autoreplyText(nullable:true, blank:false)
		question(nullable:true)
		keyword(nullable:true)
	}
	
	Poll addToMessages(Fmessage message) {
		message.messageOwner = this
		if(message.inbound) {
			this.responses.each {
				it.removeFromMessages(message)
			}
			this.responses.find { it.value == 'Unknown' }.messages.add(message)
		}
		this
	}
	
	Poll removeFromMessages(Fmessage message) {
		this.messages?.remove(this) // FIXME surely this should say remove(message)?!?!?!
		if(message.inbound) {
			this.responses.each {
				it.removeFromMessages(message)
			}
		}
		this
	}
	
	def getResponseStats() {
		def totalMessageCount = getActivityMessages().count()
		responses.sort {it.key?.toLowerCase()}.collect {
			def messageCount = it.liveMessageCount
			[id: it.id,
					value: it.value,
					count: messageCount,
					percent: totalMessageCount ? messageCount * 100 / totalMessageCount as Integer : 0]
		}
	}
	
	def editResponses(attrs) {
		if(attrs.pollType == 'standard' && !this.responses) {
			this.addToResponses(new PollResponse(value:'Yes', key:'A'))
			this.addToResponses(new PollResponse(value:'No', key:'B'))
		} else {
			def choices = attrs.findAll{ it ==~ /choice[A-E]=.*/}
			choices.each { k,v -> 
				if(this.responses*.key?.contains(k)) {
					def response = PollResponse.findByKey(k)
					if(response.value != v) {
						this.deleteResponse(response)
						this.addToResponses(new PollResponse(value: v, key:k))
					}
				} else
					if(v?.trim()) this.addToResponses(new PollResponse(value: v, key:k))	
			}
		}
		if(!this.responses*.value.contains('Unknown'))
			this.addToResponses(new PollResponse(value: 'Unknown', key: 'Unknown'))
	}
	
	def deleteResponse(PollResponse response) {
		response.messages.findAll { message ->
			this.responses.find { it.value == 'Unknown' }.messages.add(message)
		}
		this.removeFromResponses(response)
		response.delete()
		this
	}
}
