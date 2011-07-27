package frontlinesms2

class RadioShowController {
	static allowedMethods = [save: "POST"]

	def create = {

	}

	def save = {
		def showInstance = new RadioShow()
		showInstance.properties = params
		if (showInstance.validate())
			showInstance.save()
		else
			flash.message = "Name cannot be blank"
			redirect(controller: 'message', action: "inbox")
	}

}