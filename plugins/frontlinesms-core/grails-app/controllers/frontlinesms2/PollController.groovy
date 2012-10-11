package frontlinesms2

import grails.converters.JSON

class PollController extends ActivityController {
	def save() {
		// FIXME this should use withPoll to shorten and DRY the code, but it causes cascade errors as referenced here:
		// http://grails.1312388.n4.nabble.com/Cascade-problem-with-hasOne-relationship-td4495102.html
		def poll
		println "######### ${params}"
		poll = Poll.get(params.ownerId)?Poll.get(params.ownerId):new Poll()
		poll.name = params.name ?: poll.name
		poll.autoreplyText = params.enableAutoreply? (params.autoreplyText ?: poll.autoreplyText): null
		poll.question = params.question ?: poll.question
		poll.sentMessageText = params.messageText ?: poll.sentMessageText
		poll.editResponses(params)
		if (poll.save()) {
			params.enableKeyword?poll.editKeywords(params):poll.noKeyword()
			println "############ Edited keywords"
			if(!params.dontSendMessage && !poll.archived) {
				def message = messageSendService.createOutgoingMessage(params)
				message.save()
				poll.addToMessages(message)
				MessageSendJob.defer(message)
			}
			if (poll.save()) {
				params.activityId = poll.id
				if (!params.dontSendMessage)
					flash.message = message(code: 'flash.message.poll.queued')
				else
					flash.message = message(code: 'flash.message.poll.saved')

				withFormat {
					json { render([ok:true, ownerId: poll.id] as JSON)}
					html { [ownerId:poll.id]}
				}
			} else {
				renderJsonErrors(poll)
			}
		} else {
			println "Did not save at all ooooops :("
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
