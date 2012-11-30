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
		def activityInstanceToEdit
		if(params.ownerId) activityInstanceToEdit = WEB_CONNECTION_TYPE_MAP[params.imp].get(params.ownerId) 
		else activityInstanceToEdit = WEB_CONNECTION_TYPE_MAP[params.imp].newInstance()
		def responseMap = ['config', 'scripts', 'confirm'].collectEntries {
			[it, g.render(template:"/webconnection/$params.imp/$it", model:[activityInstanceToEdit:activityInstanceToEdit])]
		}
		render responseMap as JSON
	}

	def testRoute() {
		println "<<<<params>>>> $params"
		withWebconnection { webconnectionInstance ->
			doSave('webconnection', webconnectionService, webconnectionInstance, false)
			if(webconnectionService)	TestWebconnectionJob.triggerNow([webconnectionId:webconnectionInstance.id])
		}
	}

	def checkRouteStatus() {
		println "<<<params>>> $params"
		def webconnectionInstance = Webconnection.get(params.ownerId)
		def response = [ownerId:params.ownerId, ok:true]
		if(webconnectionInstance) {
			def message = Fmessage.findByMessageOwnerAndText(webconnectionInstance, Fmessage.TEST_MESSAGE_TEXT)
			response.status = message?.ownerDetail
		}

		render response as JSON
	}

	private def withWebconnection = withDomainObject WebconnectionController.WEB_CONNECTION_TYPE_MAP[params.webconnectionType]
}

