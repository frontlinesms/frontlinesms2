package frontlinesms2

class SettingsController {
	def index = {
		redirect(action:'connections')
	}
	
	def connections = {
		redirect(controller:'connection')
	}
}
