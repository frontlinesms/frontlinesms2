package frontlinesms2

import grails.converters.JSON


class WebconnectionController extends ActivityController {
	static final def WEB_CONNECTION_TYPE_MAP = [generic:GenericWebconnection,
			ushahidi:UshahidiWebconnection]

	def webconnectionService

	def create() {}

	def save() {
		withWebconnection { webconnectionInstance ->
			doSave('webconnection', webconnectionService, webconnectionInstance)
		}
	}

	def config() {
		withWebconnection { activityInstanceToEdit ->
			def responseMap = ['config', 'scripts', 'confirm'].collectEntries {
				[it, g.render(template:"/webconnection/$params.imp/$it", model:[activityInstanceToEdit:activityInstanceToEdit])]
			}
			render responseMap as JSON
		}
	}

	def retryFailed() {
		withWebconnection { c ->
			webconnectionService.retryFailed(c)
			flash.message = g.message(code: 'webconnection.failed.retried')
			redirect action:'show', params:[ownerId:c.id]
		}
	}

	private def renderJsonErrors(webconnectionInstance) {
		def errorMessages = webconnectionInstance.errors.allErrors.collect { message(error:it) }.join("\n")
		withFormat {
			json {
				render([ok:false, text:errorMessages] as JSON)
			}
		}
	}

	private def withWebconnection(Closure c) {
		Class<Webconnection> clazz = WEB_CONNECTION_TYPE_MAP[params.webconnectionType?:params.imp]?: Webconnection
		def webconnectionInstance
		if(params.ownerId) {
			webconnectionInstance = clazz.get(params.ownerId)
		} else {
			webconnectionInstance = clazz.newInstance()
		}
		if(!webconnectionInstance) handleNotFoundFailure()
		else c.call(webconnectionInstance)
	}
}

