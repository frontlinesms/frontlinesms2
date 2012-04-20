package frontlinesms2

class AnnouncementController extends ActivityController {
	def index = { redirect(action: 'save') }
	def save = {
		def announcementInstance = new Announcement()
		announcementInstance.name = params.name
		announcementInstance.sentMessageText = params.messageText
		def message = messageSendService.createOutgoingMessage(params)
		messageSendService.send(message)
		announcementInstance.addToMessages(message)
		if (announcementInstance.save()) {

			flash.message = "${message(code: 'announcement.saved')}"
			[ownerId: announcementInstance.id]
		} else {
			flash.message = "${message(code: 'announcement.not.saved')}"
			render(text: flash.message)
		}
	}

	private def withAnnouncement(Closure c) {
		def announcementInstance = Announcement.get(params.id)
		if (announcementInstance) c announcementInstance
		else render(text: "${message(code: 'announcement.id.exist.not', args: [message(code: params.id), ''])}") // TODO handle error state properly
	}
}
