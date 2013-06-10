package frontlinesms2

class SystemNotificationService {
	def i18nUtilService
	def createSystemNotification(code, args=[], kwargs=[:]) {
		if(kwargs.exception) args += [i18nUtilService.getMessage(code:'connection.error.'+kwargs.exception.class.name.toLowerCase(), args:[kwargs.exception.message])]
		def text = i18nUtilService.getMessage(code:code, args:args)
		getOrCreate(text)
	}

	private def getOrCreate(text) {
		def notification = SystemNotification.findOrCreateByText(text)
		notification.read = false
		notification.save(failOnError:true, flush:true)
	}
}
