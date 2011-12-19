package frontlinesms2.radio

import frontlinesms2.MessageController

class RadioShowController extends MessageController {
	static allowedMethods = [save: "POST"]
//	static layout = 'radioShows'
	def index = {
		redirect action:radioShow
	}
	def create = {}

	def save = {	
		def showInstance = new RadioShow()
		showInstance.properties = params
		if (showInstance.validate())
			showInstance.save()
		else
			flash.message = "Name is not valid"
			redirect(controller: 'message', action: "inbox")
	}
	
	def radioShow = {
		println "ownerId : ${params.ownerId}"
		def showInstance = RadioShow.get(params.ownerId)
		def messageInstanceList = showInstance?.getShowMessages(params.starred)
		render view:'standard', model:[messageInstanceList: messageInstanceList?.list(params),
			   messageSection: 'radioShow',
			   messageInstanceTotal: messageInstanceList?.count(),
			   ownerInstance: showInstance] << this.getShowModel()
	}
	
	def getShowModel(messageInstanceList) {
		def model = super.getShowModel(messageInstanceList)
		model << [radioShows: RadioShow.findAll()]
		return model
	}
	
}