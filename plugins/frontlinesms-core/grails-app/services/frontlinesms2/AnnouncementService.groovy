package frontlinesms2

class AnnouncementService {
	def messageSendService

    def saveInstance(Announcement announcement, params) {
		announcement.name = params.name
		announcement.sentMessageText = params.messageText
		def m = messageSendService.createOutgoingMessage(params)
		announcement.addToMessages(m)
		if(announcement.save(failOnError:true)) {
			messageSendService.send(m)
		}
		announcement
	}
}
