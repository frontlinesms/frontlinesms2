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
		text = substituteLinks(text)
		def blockedNotificationList = grailsApplication.config.frontlinesms.blockedNotificationList
		if(!(blockedNotificationList && code in blockedNotificationList)) { getOrCreate(text, params?.topic) }
	}

	private def getOrCreate(text, topic=null) {
		def notification = SystemNotification.findOrCreateByText(text)
		if(topic) {
			SystemNotification.findAllByTopic(topic).each {
				it.read = true
				it.save()
			}
			notification.topic = topic
		}
		notification.read = false
		notification.save(flush:true)
	}

	String substituteLinks(CharSequence input) {
		input.replaceAll(/\[\[([^\[]*?)\]\]\(\((.*?)\)\)([^\)]|$)/, /<a href="$2">$1<\/a>$3/)
	}
}

