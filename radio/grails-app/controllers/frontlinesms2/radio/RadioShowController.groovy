package frontlinesms2.radio

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
			flash.message = "Name is not valid"
			redirect(controller: 'message', action: "inbox")
	}

}