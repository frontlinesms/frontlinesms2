package frontlinesms2.radio

class RadioShowController {
	static allowedMethods = [save: "POST"]

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
		def showInstance = RadioShow.get(params.ownerId)
		def messageInstanceList = showInstance?.getShowMessages(params.starred)
		render view:'../message/standard', model:[messageInstanceList: messageInstanceList?.list(params),
		       messageSection: 'radioShow',
		       messageInstanceTotal: messageInstanceList?.count(),
		       ownerInstance: showInstance] << [radioShows: RadioShow.findAll()]
	}

}