package frontlinesms2

class PollController extends ActivityController {
	def save = {
		// FIXME this should use withPoll to shorten and DRY the code, but it causes cascade errors as referenced here:
		// http://grails.1312388.n4.nabble.com/Cascade-problem-with-hasOne-relationship-td4495102.html
		def poll
		if(Poll.get(params.ownerId)) {
			poll = Poll.get(params.ownerId)
			if(params.enableKeyword && params.keyword)
				poll.keyword ? poll.keyword.value = params.keyword : (poll.keyword = new Keyword(value: params.keyword))
		} else if(params.enableKeyword && params.keyword) {
			poll = new Poll(keyword: new Keyword(value: params.keyword))
		} else {
			poll = new Poll()
		}
		poll.name = params.name ?: poll.name
		poll.autoreplyText = params.autoreplyText ?: poll.autoreplyText
		poll.question = params.question ?: poll.question
		poll.sentMessageText = params.messageText ?: poll.sentMessageText
		poll.editResponses(params)
		if (poll.save(flush: true)) {
			if(!params.dontSendMessage) {
				def message = messageSendService.createOutgoingMessage(params)
				poll.addToMessages(message)
				messageSendService.send(message)
			}
			if (poll.save()) {
				if (!params.dontSendMessage)
					flash.message = message(code: 'flash.message.poll.queued')
				else
					flash.message = message(code: 'flash.message.poll.saved')
				[ownerId: poll.id]
			} else {
				flash.message = message(code: 'flash.message.poll.not.saved')
				render(text: flash.message)
			}
		} else {
			flash.message = message(code: 'flash.message.poll.not.saved')
			render(text: flash.message)
		}
	}

	def sendReply = {
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
		
	private def withPoll(Closure c) {
		def pollInstance = Poll.get(params.ownerId) ?: new Poll()
		if (pollInstance) c pollInstance
	}
}
