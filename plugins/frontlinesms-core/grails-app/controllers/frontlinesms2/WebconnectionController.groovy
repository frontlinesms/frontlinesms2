package frontlinesms2

import grails.converters.JSON

@Mixin(ControllerUtils)
class WebconnectionController extends ActivityController {
	def webconnectionService

	public static final def WEB_CONNECTION_TYPE_MAP = [generic:GenericWebconnection,
			ushahidi:UshahidiWebconnection]

	def create() {}

	def save() {
		def webconnectionInstance
		Class<Webconnection> clazz = WebconnectionController.WEB_CONNECTION_TYPE_MAP[params.webconnectionType]
		if(params.ownerId) {
			webconnectionInstance = clazz.get(params.ownerId)
		} else {
			webconnectionInstance = clazz.newInstance()
		}
		try {
			webconnectionService.saveInstance(webconnectionInstance, params)
			if(params.ownerId)
				webconnectionInstance.deactivate()
			webconnectionInstance.activate()
			flash.message = message(code:'webconnection.saved')
			params.activityId = webconnectionInstance.id
			withFormat {
				json { render([ok:true, ownerId:webconnectionInstance.id] as JSON) }
				html { [ownerId:webconnectionInstance.id] }
			}
		}
		catch(Exception e) {
			e.printStackTrace()
			def collidingKeywords = getCollidingKeywords(params.sorting == 'global'? '' : params.keywords)
			def errors
			if (collidingKeywords)
				errors = collidingKeywords.collect { 
					if(it.key == '')
						message(code:'activity.generic.global.keyword.in.use', args: [it.value])
					else
						message(code:'activity.generic.keyword.in.use', args: [it.key, it.value])
				}.join("\n")
			else
				errors = webconnectionInstance.errors.allErrors.collect {message(code:it.codes[0], args: it.arguments.flatten(), defaultMessage: it.defaultMessage)}.join("\n")
			withFormat {
				json { render([ok:false, text:errors] as JSON) }
			}
		}
	}

	def config() {
		def activityInstanceToEdit
		if(params.ownerId) activityInstanceToEdit = WEB_CONNECTION_TYPE_MAP[params.imp].get(params.ownerId) 
		else activityInstanceToEdit = WEB_CONNECTION_TYPE_MAP[params.imp].newInstance()
		def responseMap = ['config', 'scripts', 'confirm'].collectEntries {
			[it, g.render(template:"/webconnection/$params.imp/$it", model:[activityInstanceToEdit:activityInstanceToEdit])]
		}
		render responseMap as JSON
	}

	private def renderJsonErrors(webconnectionInstance) {
		def errorMessages = webconnectionInstance.errors.allErrors.collect { message(error:it) }.join("\n")
		withFormat {
			json {
				render([ok:false, text:errorMessages] as JSON)
			}
		}
	}

	private def withWebconnection = withDomainObject Webconnection
}

