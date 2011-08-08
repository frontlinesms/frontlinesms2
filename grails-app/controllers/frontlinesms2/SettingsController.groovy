package frontlinesms2

class SettingsController {
	def index = {
		redirect(action:'menu')
	}
	
	def menu = {
		
	}
	
	def connections = {
		redirect(controller:'connection')
	}
}
