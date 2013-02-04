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
			doSave('webconnection', webconnectionService, webconnectionInstance)
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
			flash.message = g.message(code: 'webconnection.failed.retried')
			redirect action:'show', params:[ownerId:c.id]
		}
	}

	def testRoute() {
		withWebconnection { webconnectionInstance ->
			doSave('webconnection', webconnectionService, webconnectionInstance, false)
			if(webconnectionService) TestWebconnectionJob.triggerNow([webconnectionId:webconnectionInstance.id])
		}
	}

	def checkRouteStatus() {
		def webconnectionInstance = Webconnection.get(params.ownerId)
		def response = [ownerId:params.ownerId, ok:true]
		if(webconnectionInstance) {
			def message = Fmessage.findByMessageOwnerAndText(webconnectionInstance, Fmessage.TEST_MESSAGE_TEXT)
			response.status = message?.ownerDetail
		}

		render response as JSON
	}

	private def withWebconnection = withDomainObject({ WEB_CONNECTION_TYPE_MAP[params.webconnectionType?:params.imp]?: Webconnection }, { params.ownerId })
}

