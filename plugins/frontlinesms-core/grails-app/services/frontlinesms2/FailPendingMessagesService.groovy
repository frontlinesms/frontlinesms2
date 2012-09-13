package frontlinesms2

class FailPendingMessagesService {
	def init() {
		// N.B. This should ONLY EVER be called in the bootstrap, and therefore probably shouldn't be a service
		def pendingDispatchList = Dispatch.findAllByStatus(DispatchStatus.PENDING)
		if (pendingDispatchList) {
			pendingDispatchList.each() {
				it.status = DispatchStatus.FAILED
				it.save()
			}
			new SystemNotification(text:"${pendingDispatchList.size()} pending message(s) failed. Go to pending messages section to view.").save(failOnError:true)
		}
		
	}
}
