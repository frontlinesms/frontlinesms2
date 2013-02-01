package frontlinesms2

class SystemNotificationService {

	def i18nUtilService

	def create(code, args, exception=null) {
		if(exception) args += [i18nUtilService.getMessage(code:'connection.error.'+exception.class.name.toLowerCase(), args:[exception.message])]
		def text = i18nUtilService.getMessage(code:code, args:args)
		def notification = SystemNotification.findByText(text) ?: new SystemNotification(text:text)
		notification.read = false
		notification.save(failOnError:true, flush:true)
	}
}