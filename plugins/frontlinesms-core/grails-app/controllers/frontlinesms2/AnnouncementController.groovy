package frontlinesms2

import grails.converters.JSON

class AnnouncementController extends ActivityController {
	def announcementService
	def index() { redirect(action: 'save') }

	def list() {
		render Announcement.findAllByArchivedAndDeleted(false, false) as JSON
	}

	def save() {
		def announcementInstance
		announcementInstance = Announcement.get(params.ownerId) ?: new Announcement()
		try{
			announcementInstance = announcementService.saveInstance(announcementInstance, params)
			flash.message = message(code: 'announcement.saved')
			params.activityId = announcementInstance.id
			withFormat {
				json { render([ok:true, ownerId: announcementInstance.id] as JSON)}
				html { [ownerId: announcementInstance.id]}
			}
		}catch(Exception e){
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
