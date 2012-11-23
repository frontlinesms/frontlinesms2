package frontlinesms2

import grails.converters.JSON


class PollController extends ActivityController {
	def pollService

	def save() {
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

	def pollStats() {
		withPoll { pollInstance ->
			render (pollInstance.responseStats as JSON)
		}
	}

	private def withPoll = withDomainObject Poll, { params.ownerId }
}

