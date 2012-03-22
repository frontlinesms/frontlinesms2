package frontlinesms2

class KeywordProcessorService {
	private static final ALPHABET = ('a'..'z').join()
	def messageSendService
	
	def process(Fmessage message) {
		processForKeyword(message)
	}
	
	def processForKeyword(Fmessage message) {
		def words = message.text.split()
		if(words.size() == 0 || (words.size() == 1 && words[0].length() < 2)) {
			return null
		} else if(words.size() == 1) {
			def word = words[0]
			def keyword = Keyword.findByValue(word)
			if(keyword)
				processForAutoReply(keyword, message)
			else {
				keyword = Keyword.findByValue(word[0..-2])
				def option = word[-1]
				processForPoll(keyword, option, message)
			}
		} else {
			def keyword = Keyword.findByValue(words[0])
			if(keyword && keyword.activity instanceof Poll)
				processForPoll(keyword, words[1], message)
			else if(keyword)
				processForAutoreply(keyword, message)
		}
	}
	
	def processForAutoreply(Keyword keyword, Fmessage message) {
		def autoreply = keyword?.activity
		def params = [:]
		params.addresses = message.src
		params.messageText = autoreply.autoreplyText
		def outgoingMessage = messageSendService.getMessagesToSend(params)
		autoreply.addToMessages(outgoingMessage)
		messageSendService.send(outgoingMessage)
		autoreply.save()
		println "Autoreply message sent to ${message.src}"
	}
	
	def processForPoll(Keyword keyword, String option, Fmessage message) {
		def response = getPollResponse(keyword, option)
		response.addToMessages(message)
		response.save(failOnError: true)
		def poll = keyword.activity
		if(poll.autoreplyText) {
			def params = [:]
			params.addresses = message.src
			params.messageText = poll.autoreplyText
			def outgoingMessage = messageSendService.getMessagesToSend(params)
			poll.addToMessages(outgoingMessage)
			messageSendService.send(outgoingMessage)
			poll.save()
			println "Autoreply message sent to ${message.src}"
		}
	}
	
	PollResponse getPollResponse(Keyword keyword, String option) {
		if(option.size() != 1 || !keyword || !(keyword.activity instanceof Poll))
			return null
		else
			return keyword.activity.responses?.getAt(getPollResponseIndex(option))
	}
	
	int getPollResponseIndex(String option) {
		assert option.size() == 1
		return ALPHABET.indexOf(option.toLowerCase())
	}
}
