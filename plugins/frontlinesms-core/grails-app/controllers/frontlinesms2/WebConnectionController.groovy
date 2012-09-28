package frontlinesms2

import grails.converters.JSON

class WebConnectionController extends ActivityController {
	private static final def WEB_CONNECTION_TYPE_MAP = [generic:GenericWebConnection,
			ushahidi:UshahidiWebConnection]

	def create() {}

	def save() {
		def webConnectionInstance
		doSave(WEB_CONNECTION_TYPE_MAP[params.webConnectionType])
	}

	def config() {
		def activityInstanceToEdit
		if(params.ownerId) activityInstanceToEdit = WEB_CONNECTION_TYPE_MAP[params.imp].get(params.ownerId) 
		else activityInstanceToEdit = WEB_CONNECTION_TYPE_MAP[params.imp].newInstance()
		def responseMap = ['config', 'scripts', 'confirm'].collectEntries {
			[it, g.render(template:"/webConnection/$params.imp/$it", model:[activityInstanceToEdit:activityInstanceToEdit])]
		}
		render responseMap as JSON
	}

	private def doSave(Class<WebConnection> clazz) {
		def webConnectionInstance
		if(params.ownerId) { 
			webConnectionInstance = clazz.get(params.ownerId)
			def keywordValue = params.blankKeyword ? '' : params.keyword.toUpperCase()
			webConnectionInstance.keyword.value = keywordValue
		} else {
			webConnectionInstance = clazz.newInstance()
			webConnectionInstance.keyword =  new Keyword(value: params.blankKeyword ? '' : params.keyword.toUpperCase())
		}
		webConnectionInstance.initialize(params)
		
		if(webConnectionInstance.save(flush:true)) {
			if(params.ownerId)
				webConnectionInstance.deactivate()
			webConnectionInstance.activate()
			flash.message = message(code:'webConnection.saved')
			params.activityId = webConnectionInstance.id
			withFormat {
				json { render([ok:true, ownerId:webConnectionInstance.id] as JSON) }
				html { [ownerId:webConnectionInstance.id] }
			}
		} else {
			def errors = webConnectionInstance.errors.allErrors.collect {message(code:it.codes[0], args: it.arguments.flatten(), defaultMessage: it.defaultMessage)}.join("\n")
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

	private def withWebConnection(Closure c) {
		def webConnectionInstance = WebConnection.get(params.id)
		if (webConnectionInstance) c webConnectionInstance
		else render(text: message(code:'activity.id.exist.not', args: [message(code: params.id), ''])) // TODO handle error state properly
	}
}
