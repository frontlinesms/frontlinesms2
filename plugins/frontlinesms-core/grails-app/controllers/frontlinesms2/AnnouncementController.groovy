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
		doSave(announcementService, announcementInstance)
	}
}

