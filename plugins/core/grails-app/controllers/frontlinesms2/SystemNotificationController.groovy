package frontlinesms2

import grails.converters.*

class SystemNotificationController {
	
	def markRead = {
		withNotification {
			it.read = true
			it.save(failOnError:true)
			render text: message(code: 'system.notification.ok')
		}
	}
	
	def list = {
		def notifications = SystemNotification.findAllByRead(false)
		def data = notifications.collectEntries { [it.id, it.text] }
		render data as JSON
	}
	
	private def withNotification(Closure c) {
		SystemNotification s = SystemNotification.get(params.id)
		if(s) {
			c.call(s)
		} else {
			render text: message(code: 'system.notification.fail')
		}
	}
}
