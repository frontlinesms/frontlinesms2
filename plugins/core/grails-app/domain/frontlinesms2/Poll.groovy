package frontlinesms2

import java.util.Date;

class Poll {
	String title
	String keyword
	String autoReplyText
	String question
	String messageText
	boolean archived
	boolean deleted
	Date dateCreated
	List responses
	static transients = ['liveMessageCount']

	static hasMany = [responses: PollResponse, messages: Fmessage]

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
		messageText(nullable:true)
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

	def getPollMessages(getOnlyStarred=false) {
		Fmessage.owned(getOnlyStarred, this.responses)
	}

	def getResponseStats() {
		def totalMessageCount = getPollMessages(false).count()
		responses.sort {it.key?.toLowerCase()}.collect {
			def messageCount = it.liveMessageCount
			[id: it.id,
					value: it.value,
					count: messageCount,
					percent: totalMessageCount ? messageCount * 100 / totalMessageCount as Integer : 0]
		}
	}
	
	def archivePoll() {
		this.archived = true
		def messagesToArchive = Fmessage.owned(this.responses, true).list()
		messagesToArchive.each { it.archived = true }
	}
	
	def unarchivePoll() {
		this.archived = false
		def messagesToUnarchive = Fmessage.owned(false, this.responses, true).list()
		messagesToUnarchive.each { it.archived = false }
	}

	static Poll createPoll(attrs) {
		def poll = new Poll(attrs)
		if(attrs['poll-type'] == 'standard') {	['Yes','No'].each { poll.addToResponses(new PollResponse(value:it, key:it)) }
		} else {
			def choices = attrs.findAll{ it ==~ /choice[A-E]=.*/}
			choices.each { k,v -> 
				if(v) poll.addToResponses(new PollResponse(value: v, key:k))
			}
		}
		poll.addToResponses(new PollResponse(value: 'Unknown', key: 'Unknown'))
		poll
	}
	
	def getLiveMessageCount() {
		def messageTotal = 0
		responses.each { messageTotal += (it.liveMessageCount ?: 0) }
		messageTotal
	}
	
	def addToMessages(message) {
		this.responses.find { it.value == 'Unknown' }.addToMessages(message)
	}
}
