package frontlinesms2

class PollController extends ActivityController {
	def pollService

	def save() {
		params.keywords = params.topLevelKeyword?: "$params.keywordsA,$params.keywordsB,$params.keywordsC,$params.keywordsD,$params.keywordsE"
		withPoll { poll ->
			doSave('poll', pollService, poll)
		}
	}

	def sendReply() {
		def poll = Poll.get(params.ownerId)
		def incomingMessage = Fmessage.get(params.messageId)
		if(poll.autoreplyText) {
			params.addresses = incomingMessage.src
			params.messageText = poll.autoreplyText
			def outgoingMessage = messageSendService.createOutgoingMessage(params)
			poll.addToMessages(outgoingMessage)
			messageSendService.send(outgoingMessage)
			poll.save()
		}
		render ''
	}

	private def withPoll = withDomainObject Poll, { params.ownerId }
}

