package frontlinesms2

import frontlinesms2.*

class AnnouncementService {
	def messageSendService

    def saveInstance(Announcement announcement, params) {
		announcement.name = params.name
		announcement.sentMessageText = params.messageText
		def m = messageSendService.createOutgoingMessage(params)
		println "text ### ${m.text}"
		println "inbound ### ${m.inbound}"
		println "params ### ${params}"
		messageSendService.send(m)
		announcement.addToMessages(m)
		announcement.save(failOnError:true,flush:true)
		return announcement
	}
}
