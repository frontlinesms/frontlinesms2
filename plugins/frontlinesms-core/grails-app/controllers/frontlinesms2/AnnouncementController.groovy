package frontlinesms2

import grails.converters.JSON

class AnnouncementController extends ActivityController {
	def index() { redirect(action: 'save') }

	def list() {
		render Announcement.getAll() as JSON
	}

	def save() {
		def announcementInstance
		announcementInstance = Announcement.get(params.ownerId) ?: new Announcement()
		announcementInstance.name = params.name
		announcementInstance.sentMessageText = params.messageText
		def m = messageSendService.createOutgoingMessage(params)
		messageSendService.send(m)
		announcementInstance.addToMessages(m)
		if (announcementInstance.save()) {
			flash.message = message(code: 'announcement.saved')
			params.activityId = announcementInstance.id
			withFormat {
				json { render([ok:true, ownerId: announcementInstance.id] as JSON)}
				html { [ownerId: announcementInstance.id]}
			}

		} else {
			def errors = announcementInstance.errors.allErrors.collect { message(error:it)}.join("\n")
			withFormat {
				json { render([ok:false, text:errors] as JSON)}
			}
		}
	}

	private def withAnnouncement(Closure c) {
		def announcementInstance = Announcement.get(params.id)
		if (announcementInstance) c announcementInstance
		else render(text: message(code:'announcement.id.exist.not', args:[message(code:params.id), ''])) // TODO handle error state properly
	}
}
