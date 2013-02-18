package frontlinesms2

import grails.converters.*


class SystemNotificationController extends ControllerUtils {
	def markRead() {
		withNotification {
			it.read = true
			it.save(failOnError:true)
			render text: message(code: 'system.notification.ok')
		}
	}

	private def withNotification = withDomainObject SystemNotification
}

