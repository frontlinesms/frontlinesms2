package frontlinesms2

class SystemNotificationService {
	def i18nUtilService
	def grailsApplication

	def create(Map params) {
		def code = params?.code
		def args = params?.args
		def kwargs = params?.kwargs ?: [:]
		if(kwargs.exception) args += [i18nUtilService.getMessage(code:'connection.error.'+kwargs.exception.class.name.toLowerCase(), args:[kwargs.exception.message])]
		def text = i18nUtilService.getMessage(code:code, args:args)
		def blockedNotificationList = grailsApplication.config.frontlinesms.blockedNotificationList
		println "SystemNotificationService.create()::: blockedNotificationList::$blockedNotificationList"
		if(!(blockedNotificationList && code in blockedNotificationList)) { getOrCreate(text, params?.topic) }
		
	}

	private def getOrCreate(text, topic=null) {
		def notification = SystemNotification.findOrCreateByText(text)
		notification.read = false
		notification.topic = topic
		notification.save(flush:true)
	}
}
