package frontlinesms2

class RadioShowController {
	static allowedMethods = [save: "POST"]

	def create = {

	}

	def save = {
		def showInstance = new RadioShow()
		showInstance.properties = params
		showInstance.save()
		redirect(controller: 'message', action: "radioShow", ownerId: showInstance.id)
	}

}