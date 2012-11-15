package frontlinesms2

import grails.converters.JSON

class AnnouncementController extends ActivityController {
	def announcementService

	def index() { redirect(action:'save') }

	def list() {
		render Announcement.findAllByArchivedAndDeleted(false, false) as JSON
	}

	def save() {
		def announcementInstance = Announcement.get(params.ownerId)?: new Announcement()
		doSave('announcement', announcementService, announcementInstance)
	}

	private def withAnnouncement(Closure c) {
		def announcementInstance = Announcement.get(params.id)
		if (announcementInstance) c announcementInstance
		else render(text: message(code:'announcement.id.exist.not', args:[message(code:params.id), ''])) // TODO handle error state properly
	}
}

