package frontlinesms2

class SystemNotificationService {
	def i18nUtilService
	def create(Map params) {
		if(params?.kwargs?.exception) params?.args += [i18nUtilService.getMessage(code:'connection.error.'+params.kwargs.exception.class.name.toLowerCase(), args:[params.kwargs.exception.message])]
		def text = i18nUtilService.getMessage(code:params?.code, args:params?.args)
		getOrCreate(text)
	}

	private def getOrCreate(text) {
		def notification = SystemNotification.findOrCreateByText(text)
		notification.read = false
		notification.save(flush:true)
	}
}
