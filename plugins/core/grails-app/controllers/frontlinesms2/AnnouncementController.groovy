package frontlinesms2

class AnnouncementController extends ActivityController {
	def index = { redirect(action: 'save') }
	def save = {
		def announcementInstance = new Announcement()
		announcementInstance.name = params.name
		announcementInstance.sentMessageText = params.messageText
		def message = messageSendService.getMessagesToSend(params)
		messageSendService.send(message)
		announcementInstance.addToMessages(message)
		announcementInstance.save()
		flash.message = "Announcement has been saved and message(s) have been queued to send"
		[ownerId: announcementInstance.id]
	}

	private def withAnnouncement(Closure c) {
		def announcementInstance = Announcement.get(params.id)
		if (announcementInstance) c announcementInstance
		else render(text: "Could not find announcement with id ${params.id}") // TODO handle error state properly
	}
}
