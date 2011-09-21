package frontlinesms2

import grails.util.GrailsConfig

class ArchiveController extends MessageController {
	def beforeInterceptor = {
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		params.offset  = params.offset ?: 0
		params.viewingArchive = true
		true
	}
	
	def index = {
		redirect(action:'inbox')
	}
	
	def pollView = {
		render view:'standard', model:[pollInstanceList: Poll.findAllByArchived(true),
											actionLayout : "archive",
											messageSection: "poll"]
	}
	
	
}
