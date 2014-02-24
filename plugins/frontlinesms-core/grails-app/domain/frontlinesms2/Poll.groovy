package frontlinesms2

class Poll extends Activity {
//> CONSTANTS
	private static final String ALPHABET = ('A'..'Z').join()
	static final String KEY_UNKNOWN = 'unknown'
	static String getShortName() { 'poll' }

//> SERVICES
	def messageSendService
	def pollService

//> PROPERTIES
	String autoreplyText
	String question
	boolean yesNo
	static hasMany = [responses: PollResponse]
	SortedSet responses

//> SETTINGS
	static transients = ['unknown']
	
	static mapping = {
		keyword cascade: 'all'
		version false
	}

	def getDisplayText(TextMessage msg) {
		def p = responses.find { "$it.id" == msg.ownerDetail }
		p? "${p.value} (\"${msg.text}\")": msg.text
	}
			
	static constraints = {
		name(blank:false, maxSize:255, validator: { val, obj ->
			if(obj?.deleted || obj?.archived) return true
			def identical = Poll.findAllByNameIlike(val)
			if(!identical) return true
			else if (identical.any { it.id != obj.id && !it?.archived && !it?.deleted }) return false
			else return true
			})
		responses(validator: { val, obj ->
			val?.size() > 2 &&
					(val*.value as Set)?.size() == val?.size()
		})
		autoreplyText(nullable:true, blank:false)
		question(nullable:true)
		keywords(nullable:true)
	}

//> ACCESSORS
	def getUnknown() {
		responses.find { it.key == KEY_UNKNOWN } }
	
	Poll addToMessages(TextMessage message) {
		if(!messages) messages = []
		messages << message
		message.messageOwner = this
		this.save()
		if(message.inbound) {
			this.responses.each {
				it.removeFromMessages(message)
			}
			this.unknown.addToMessages(message)
		}
		this
	}
	
	Poll removeFromMessages(TextMessage message) {
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
		def totalMessageCount = messages?.count { it.inbound && !it.isDeleted && (it.archived == this.archived) }?: 0
		responses.collect {
			def messageCount = it.liveMessageCount
			[id: it.id,
					value: it.value,
					count: messageCount,
					percent: totalMessageCount ? new BigDecimal(messageCount * 100 / totalMessageCount).setScale(2,BigDecimal.ROUND_HALF_UP) : 0]
		}
	}
	
	def editResponses(attrs) {
		if(attrs.pollType == 'yesNo' && !this.responses) {
			this.addToResponses(key:'A', value:'Yes')
			this.addToResponses(key:'B', value:'No')
			this.yesNo = true
		} else {
			def choices = attrs.findAll { it ==~ /choice[A-E]=.*/ }
			choices.each { k, v ->
				k = k.substring('choice'.size())
				def found = responses.find { it.key == k }
				if(found) {
					found.value = v
					println "###### Key :: ${k}"
					println "###### value :: ${v}"
				} else if(v?.trim()) {
					println "###### Adding a new PollResponse ${k} :: ${v}"
					this.addToResponses(key:k, value:v)
				}
			}
		}
		if(!this.unknown) {
			this.addToResponses(PollResponse.createUnknown())
		}
	}
//TODO edit keywords only adds keywoids to the poll...does not edit
	def editKeywords(attrs){
		this.keywords?.clear()
		def keys = attrs.findAll { it ==~ /keywords[A-E]=.*/ }
		println "###### Keywords :: ${keys}"
		if(attrs.topLevelKeyword?.trim().length() > 0) {
			attrs.topLevelKeyword?.replaceAll(/\s/, "").trim().split(",").each{
				println "## Setting Poll topLevelKeyword # $it"
				this.addToKeywords(new Keyword(value:"${it.trim().toUpperCase()}"))
			}
		}
		println "Keywords after setting Most TopLevel ## ${this.keywords*.value}"
		keys.each { k, v ->
			println "${k}"
			k = k.substring('keywords'.size())
			println "###### K :: ${k}"
			println "###### V :: ${v}"
			println attrs["keywords${k}"]
			attrs["keywords${k}"]?.replaceAll(/\s/, "").split(",").each{
				if(it.size() > 0)
					this.addToKeywords(new Keyword(value:"${it.toUpperCase()}", ownerDetail:"${k}", isTopLevel:!(attrs.topLevelKeyword?.trim().length() > 0)))//adds the keyword without setting the ownerDetail as pollResponse.id
			}
		}
	}

	def noKeyword(){
		println "Removing the keywords"
	}

	def extractAliases(attrs, String k) {
		def raw = attrs["alias$k"]
		if(raw) raw.toUpperCase().replaceAll(/\s/, "").split(",").findAll { it }.join(", ")
	}

	def processKeyword(TextMessage message, Keyword keyword) {
		def response = getPollResponse(message, keyword)
		response?.addToMessages(message)
		response?.save()
		if(this.autoreplyText) {
			pollService.sendPollReply(this, message)
		}
		this.save(failOnError:true)
	}
	
	def PollResponse getPollResponse(TextMessage message, Keyword keyword) {
		if(!keyword || (keyword?.isTopLevel && !keyword?.ownerDetail)){
			return this.unknown
		} else {
			return this.responses.find { keyword.ownerDetail == it.key }
		}
	}

	def deleteResponse(PollResponse response) {
		response.messages.findAll { message ->
			this.unknown.addToMessages(message)
		}
		this.removeFromResponses(response)
		response.delete()
		this
	}

}

