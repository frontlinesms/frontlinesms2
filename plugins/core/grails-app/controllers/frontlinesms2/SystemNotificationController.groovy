package frontlinesms2

import grails.converters.*

class SystemNotificationController {
	
	def markRead = {
		withNotification {
			it.read = true
			it.save(failOnError:true)
			render text:'OK'
		}
	}
	
	def list = {
		def systemNotificationInstanceList
		def notificationIdList = params.notificationIdList.tokenize(",")?.collect{ it.toLong()}
		if(notificationIdList) {
			systemNotificationInstanceList = SystemNotification.findAllByReadAndIdNotInList(false, notificationIdList)
		} else {
			systemNotificationInstanceList = SystemNotification.findAllByRead(false)
		}
		def notifications = systemNotificationInstanceList?.collect {
			[
				markRead:" ${remoteLink(controller:'systemNotification', action:'markRead', id:it.id){'mark read'}}",
				text:it.text,
				id:it.id
			]
		}
		render notifications as JSON
		
		
	}
	
	private def withNotification(Closure c) {
		SystemNotification s = SystemNotification.get(params.id)
		if(s) {
			c.call(s)
		} else {
			render text:'FAIL'
		}
	}
}
