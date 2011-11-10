package frontlinesms2

class SystemNotificationController {
	def markRead = {
		withNotification {
			it.read = true
			it.save(failOnError:true)
			render text:'OK'
		}
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
