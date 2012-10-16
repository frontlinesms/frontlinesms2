package frontlinesms2

import grails.converters.JSON
class AutoreplyController extends ActivityController {

	def autoreplyService

	def save() {
		// FIXME this should use withAutoreply to shorten and DRY the code, but it causes cascade errors as referenced here:
		// http://grails.1312388.n4.nabble.com/Cascade-problem-with-hasOne-relationship-td4495102.html
		def autoreply
		if(Autoreply.get(params.ownerId))
			autoreply = Autoreply.get(params.ownerId)
		else
			autoreply = new Autoreply()
		try { 
			autoreplyService.saveInstance(autoreply, params)
			flash.message = message(code:'autoreply.saved')
			params.activityId = autoreply.id
			withFormat {
				json { render([ok:true, ownerId:autoreply.id] as JSON) }
				html { [ownerId:autoreply.id] }
			}
		}
		catch (Exception e) {
			def errors = autoreply.errors.allErrors.collect {message(code:it.codes[0], args: it.arguments.flatten(), defaultMessage: it.defaultMessage)}.join("\n")
			withFormat {
				json { render([ok:false, text:errors] as JSON) }
			}
		}
	}

	def sendReply() {
		def autoreply = Autoreply.get(params.ownerId)
		def incomingMessage = Fmessage.get(params.messageId)
		params.addresses = incomingMessage.src
		params.messageText = autoreply.autoreplyText
		def outgoingMessage = messageSendService.createOutgoingMessage(params)
		autoreply.addToMessages(outgoingMessage)
		messageSendService.send(outgoingMessage)
		autoreply.save()
		render ''
	}
}

