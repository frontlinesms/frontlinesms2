package frontlinesms2

import grails.converters.JSON

class PollController extends ActivityController {
	def pollService
	def save() {
		def poll = Poll.get(params.ownerId)?Poll.get(params.ownerId):new Poll()
		try{
			poll = pollService.saveInstance(poll, params)
			params.activityId = poll.id
			if (!params.dontSendMessage)
			       flash.message = message(code: 'flash.message.poll.queued')
			else
			       flash.message = message(code: 'flash.message.poll.saved')

			withFormat {
			       json { render([ok:true, ownerId: poll.id] as JSON)}
			       html { [ownerId:poll.id]}
			}
		} catch(Exception e){
			println e
			println "## Exception Thrown"
			renderJsonErrors(poll)
		}
	}

	private def renderJsonErrors(poll) {
		def errorMessages = poll.errors.allErrors.collect { message(error:it) }.join("\n")
		withFormat {
			json {
				render([ok:false, text:errorMessages] as JSON)
			}
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
		
	private def withPoll(Closure c) {
		def pollInstance = Poll.get(params.ownerId) ?: new Poll()
		if (pollInstance) c pollInstance
	}
}
