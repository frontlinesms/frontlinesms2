package frontlinesms2

import grails.converters.JSON

class WebconnectionController extends ActivityController {
	static final def WEB_CONNECTION_TYPE_MAP = [generic:GenericWebconnection,
			ushahidi:UshahidiWebconnection]

	def webconnectionService

	def create() {}

	def save() {
		doSave('webconnection', webconnectionService, getWebconnectionInstance())
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
		def webconnectionInstance = getWebconnectionInstance()
		doSave('webconnection', webconnectionService, webconnectionInstance, false)
		TestWebconnectionJob.triggerNow([webconnectionId:webconnectionInstance.id])
		return
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

	private def getWebconnectionInstance() {
		def webconnectionInstance
		Class<Webconnection> clazz = WebconnectionController.WEB_CONNECTION_TYPE_MAP[params.webconnectionType]
		if(params.ownerId) {
			webconnectionInstance = clazz.get(params.ownerId)
		} else {
			webconnectionInstance = clazz.newInstance()
		}

	}
}

