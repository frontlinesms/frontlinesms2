package frontlinesms2

class Poll extends Activity {
//> CONSTANTS
	private static final String ALPHABET = ('A'..'Z').join()
	static final String KEY_UNKNOWN = 'unknown'
	static String getShortName() { 'poll' }

//> SERVICES
	def messageSendService

//> PROPERTIES
	static hasOne = [keyword: Keyword]
	String autoreplyText
	String question
	boolean isStandard
	List responses
	static hasMany = [responses: PollResponse]

//> SETTINGS
	static transients = ['unknown']
	
	static mapping = {
		keyword cascade: 'all'
	}
			
	static constraints = {
		name(blank:false, maxSize:255, unique:true)
		responses(validator: { val, obj ->
			val?.size() > 2 &&
					(val*.value as Set)?.size() == val?.size()
		})
		autoreplyText(nullable:true, blank:false)
		question(nullable:true)
		keyword(nullable:true)
	}

//> ACCESSORS
	def getUnknown() {
		responses.find { it.key == KEY_UNKNOWN } }
	
	Poll addToMessages(Fmessage message) {
		if(!messages) messages = []
		messages << message
		message.messageOwner = this
		if(message.inbound) {
			this.responses.each {
				it.removeFromMessages(message)
			}
			this.unknown.messages.add(message)
		}
		this
	}
	
	Poll removeFromMessages(Fmessage message) {
		this.messages?.remove(message)
		if(message.inbound) {
			this.responses.each {
				it.removeFromMessages(message)
			}
		}
		message.messageOwner = null
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
			this.addToResponses(value:'Yes', key:'A')
			this.addToResponses(value:'No', key:'B')
			this.isStandard = true
		} else {
			def choices = attrs.findAll { it ==~ /choice[A-E]=.*/ }
			choices.each { k, v ->
				k = k.substring('choice'.size())
				def found = responses.find { it.key == k }
				if(found) {
					found.value = v
				} else if(v?.trim()) this.addToResponses(value:v, key:k)
			}
		}
		if(!this.unknown) {
			this.addToResponses(PollResponse.createUnknown())
		}
	}
	
	def deleteResponse(PollResponse response) {
		response.messages.findAll { message ->
			this.unknown.messages.add(message)
		}
		this.removeFromResponses(response)
		response.delete()
		this
	}

	def processKeyword(Fmessage message, boolean exactMatch) {
		def response = getPollResponse(message, exactMatch)
		response.addToMessages(message)
		response.save()
		def poll = this
		if(poll.autoreplyText) {
			def params = [:]
			params.addresses = message.src
			params.messageText = poll.autoreplyText
			def outgoingMessage = messageSendService.createOutgoingMessage(params)
			poll.addToMessages(outgoingMessage)
			messageSendService.send(outgoingMessage)
			poll.save()
		}
	}
	
//> PRIVATE HELPERS
	private PollResponse getPollResponse(Fmessage message, boolean exactMatch) {
		def option
		def words = message.text.trim().toUpperCase().split(/\s/)
		if(exactMatch) {
			if(words.size() < 2) return this.unknown
			option = words[1]
			if(option.size() > 1) return this.unknown
		} else {
			option = words[0][-1]
		}
		return responses.find { it.key == option }?: this.unknown
	}
}

