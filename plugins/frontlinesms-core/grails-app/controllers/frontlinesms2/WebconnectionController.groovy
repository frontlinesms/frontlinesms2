package frontlinesms2

import grails.converters.JSON

class WebconnectionController extends ActivityController {
	static final def WEB_CONNECTION_TYPE_MAP = [generic:GenericWebconnection,
			ushahidi:UshahidiWebconnection]

	def webconnectionService

	def create() {}

	def save() {
		if(params.activityId) params.ownerId = params.activityId
		withWebconnection { webconnectionInstance ->
			doSave(webconnectionService, webconnectionInstance)
		}
	}

	def config() {
		withWebconnection { activityInstanceToEdit ->
			def responseMap = ['config', 'scripts', 'confirm'].collectEntries {
				[it, fsms.render(template:"/webconnection/$params.imp/$it", model:[activityInstanceToEdit:activityInstanceToEdit])]
			}
			render responseMap as JSON
		}
	}

	def retryFailed() {
		withWebconnection { c ->
			webconnectionService.retryFailed(c)
			flashMessage = g.message(code: 'webconnection.failed.retried')
			redirect action:'show', params:[ownerId:c.id]
		}
	}

	def testRoute() {
		withWebconnection { webconnectionInstance ->
			doSave(webconnectionService, webconnectionInstance, false)
			TestWebconnectionJob.triggerNow([webconnectionId:webconnectionInstance.id])
		}
	}

	private def withWebconnection = withDomainObject({ WEB_CONNECTION_TYPE_MAP[params.webconnectionType?:params.imp]?: Webconnection }, { params.ownerId })
}

