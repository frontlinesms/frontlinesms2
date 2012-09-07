package frontlinesms2

import grails.converters.JSON

class WebConnectionController extends ActivityController {
	def create = {}

	def save = {
		def webConnectionInstance
		if(params.ownerId) { 
			webConnectionInstance = WebConnection.get(params.ownerId)

			def keywordValue = params.blankKeyword ? '' : params.keyword.toUpperCase()
			webConnectionInstance.keyword.value = keywordValue
		} else {
			webConnectionInstance = new WebConnection()
			webConnectionInstance.keyword =  new Keyword(value: params.blankKeyword ? '' : params.keyword.toUpperCase())
		}

		webConnectionInstance.httpMethod = WebConnection.HttpMethod."${params.httpMethod.toUpperCase()}"
		webConnectionInstance.url = params.url
		webConnectionInstance.name = params.name
		processRequestParameters(webConnectionInstance)
		if(webConnectionInstance.save(flush:true)) {
			flash.message = message(code:'webConnection.saved')
			params.activityId = webConnectionInstance.id
			withFormat {
				json { render([ok:true, ownerId:webConnectionInstance.id] as JSON) }
				html { [ownerId:webConnectionInstance.id] }
			}
		} else {
			def errors = webConnectionInstance.errors.allErrors.collect {message(code:it.codes[0], args: it.arguments.flatten(), defaultMessage: it.defaultMessage)}.join("\n")
			println "the errors are :: $errors"
			withFormat {
				json { render([ok:false, text:errors] as JSON) }
			}
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

	private def processRequestParameters(webConnectionInstance) {
		params.'params-name'?.size()?.times {
			def name = params.'params-name'[it].trim()
			def value = params.'params-value'[it].trim()
			def found = webConnectionInstance.requestParameters.find { it.name == name}
			if(found) {
				found.value = value
			} else if(name) {
				def requestParam = new RequestParameter(name:name, value:value)
				webConnectionInstance.addToRequestParameters(requestParam)
			}
		}
	}

	private def withWebConnection(Closure c) {
		def webConnectionInstance = WebConnection.get(params.id)
		if (webConnectionInstance) c webConnectionInstance
		else render(text: message(code:'activity.id.exist.not', args: [message(code: params.id), ''])) // TODO handle error state properly
	}
}