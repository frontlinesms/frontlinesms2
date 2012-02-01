package frontlinesms2

class KeywordProcessorService {
	private static final ALPHABET = "abcdefghijklmnopqrstuvwxyz"
	def messageSendService
	
	def process(Fmessage message) {
		processPollResponse(message)
			// || processOtherStuff()
	}
	
	def processPollResponse(Fmessage message) {
		def pollResponse = getPollResponse(message.text)
		if(pollResponse != null) {
			processPollResponse(pollResponse, message)
			return true
		} else return false
	}
	
	def processPollResponse(PollResponse response, Fmessage message) {
		response.addToMessages(message)
		response.save(failOnError: true)
		def poll = response.poll
		if(poll.autoReplyText) {
			params.addresses = params.message.src
			params.messageText = poll.autoReplyText
			def outgoingMessage = messageSendService.getMessagesToSend(params)
			poll.addToMessages(outgoingMessage)
			messageSendService.send(outgoingMessage)
			poll.save()
			println "Autoreply message sent to ${message.src}"
		}
		return true
	}

	PollResponse getPollResponse(String messageText) {
		def words = messageText.split()
		if(words.size() == 0 || (words.size() == 1 && words[0].length() < 2)) {
			return null
		} else if(words.size() == 1) {
			def word = words[0]
			def keyword = word[0..-2]
			def option = word[-1]
			return getPollResponse(keyword, option)
		} else {
			def keyword = words[0]
			def option = words[1]
			def pollResponse = getPollResponse(keyword, option)
			if(pollResponse) return pollResponse
			else return getPollResponse(keyword) // TODO should do this in a single select
		}
	}
	
	PollResponse getPollResponse(String keyword, String option) {
		if(option.size() != 1 || keyword.size() < 1) {
			return null
		} else {
			return Poll.findByKeywordIlike(keyword)?.responses?.getAt(getPollResponseIndex(option))
		}
	}
	
	int getPollResponseIndex(String option) {
		assert option.size() == 1
		return ALPHABET.indexOf(option.toLowerCase())
	}
}
