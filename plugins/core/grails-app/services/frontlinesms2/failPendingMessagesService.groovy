package frontlinesms2

class failPendingMessagesService {
	def init() {
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
