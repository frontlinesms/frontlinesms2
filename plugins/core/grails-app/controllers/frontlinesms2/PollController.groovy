package frontlinesms2

class PollController extends ActivityController {

	def save = {
		withPoll { poll ->
			poll.name = params.name
			poll.autoreplyText = params.autoreplyText
			poll.question = params.question
			poll.sentMessageText = params.messageText
			if(params.enableKeyword && params.keyword) poll.keyword = new Keyword(value: params.keyword)
			poll.editResponses(params)
			poll.save(flush: true, failOnError: true)
			if(!params.dontSendMessage) {
				def message = messageSendService.getMessagesToSend(params)
				poll.addToMessages(message)
				messageSendService.send(message)
				poll.save()
				flash.message = "Poll has been saved and message(s) has been queued to send"
			} else {
				poll.save()
				flash.message = "Poll has been saved"
			}
			[ownerId: poll.id]
		}
	}
	
	def sendReply = {
		def poll = Poll.get(params.pollId)
		def incomingMessage = Fmessage.get(params.messageId)
		if(poll.autoreplyText) {
			params.addresses = incomingMessage.src
			params.messageText = poll.autoreplyText
			def outgoingMessage = messageSendService.getMessagesToSend(params)
			poll.addToMessages(outgoingMessage)
			messageSendService.send(outgoingMessage)
			poll.save()
		}
		render ''
	}
		
	private def withPoll(Closure c) {
		def pollInstance = Poll.get(params.ownerId) ?: new Poll()
		if (pollInstance) c pollInstance
	}
}
