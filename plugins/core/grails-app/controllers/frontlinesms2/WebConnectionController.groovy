package frontlinesms2

import grails.converters.JSON

class WebConnectionController extends ActivityController {
	def create = {}

	def save = {
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