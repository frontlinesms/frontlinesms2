package frontlinesms2

import grails.converters.JSON

class WebConnectionController extends ActivityController {
	def create = {}

	def save = {
		if(WebConnection.get(params.ownerId)) {
			webConnectionInstance = WebConnection.get(params.ownerId)
			if(params.enableKeyword && params.keyword)
				webConnectionInstance.keyword ? webConnectionInstance.keyword.value = params.keyword : (webConnectionInstance.keyword = new Keyword(value: params.keyword.toUpperCase()))
		} else if(params.enableKeyword && params.keyword) {
			webConnectionInstance = new WebConnection()
			webConnectionInstance.keyword = new Keyword(value: params.keyword.toUpperCase())
		} else {
			webConnectionInstance = new WebConnection()
		}
		webConnectionInstance.name = params.name ?: webConnectionInstance.name
		webConnectionInstance.autoreplyText = params.enableAutoreply? (params.autoreplyText ?: webConnectionInstance.autoreplyText): null
		webConnectionInstance.question = params.question ?: webConnectionInstance.question
		webConnectionInstance.sentMessageText = params.messageText ?: webConnectionInstance.sentMessageText
		webConnectionInstance.editResponses(params)
		if (webConnectionInstance.save(flush:true)) {
			if(!params.dontSendMessage && !webConnectionInstance.archived) {
				def message = messageSendService.createOutgoingMessage(params)
				message.save()
				webConnectionInstance.addToMessages(message)
				MessageSendJob.defer(message)
			}
			if (webConnectionInstance.save()) {
				params.activityId = webConnectionInstance.id
				if (!params.dontSendMessage)
					flash.message = message(code: 'flash.message.webConnectionInstance.queued')
				else
					flash.message = message(code: 'flash.message.webConnectionInstance.saved')

				withFormat {
					json { render([ok:true, ownerId: webConnectionInstance.id] as JSON)}
					html { [ownerId:webConnectionInstance.id]}
				}
			} else {
				renderJsonErrors(webConnectionInstance)
			}
		} else {
			renderJsonErrors(webConnectionInstance)
		}
	}

	private def renderJsonErrors(webConnectionInstance) {
		def errorMessages = webConnectionInstance.errors.allErrors.collect { message(error:it) }.join("\n")
		withFormat {
			json {
				render([ok:false, text:errorMessages] as JSON)
			}
		}
	}


	private def withWebConnection(Closure c) {
		def webConnectionInstance = WebConnection.get(params.id)
		if (webConnectionInstance) c webConnectionInstance
		else render(text: message(code:'activity.id.exist.not', args: [message(code: params.id), ''])) // TODO handle error state properly
	}
}