package frontlinesms2

import grails.converters.JSON

class WebconnectionController extends ActivityController {
	static final def WEB_CONNECTION_TYPE_MAP = [generic:GenericWebconnection,
			ushahidi:UshahidiWebconnection]

	def webconnectionService

	def create() {}

	def save() {
		def webconnectionInstance
		Class<Webconnection> clazz = WebconnectionController.WEB_CONNECTION_TYPE_MAP[params.webconnectionType]
		if(params.ownerId) {
			webconnectionInstance = clazz.get(params.ownerId)
		} else {
			webconnectionInstance = clazz.newInstance()
		}
		doSave('webconnection', webconnectionService, webconnectionInstance)
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

