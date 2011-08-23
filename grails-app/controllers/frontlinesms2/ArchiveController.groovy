package frontlinesms2

class ArchiveController extends MessageController {
	def beforeInterceptor = {
		params.max = params.max ?: getPaginationCount()
		params.offset  = params.offset ?: 0
		params.archived = true
		true
	}
	
	def index = {
		redirect(action:'inbox')
	}
	
	def poll = {
		render view:'standard', model:[polls: Poll.getArchivedPolls(),
											actionLayout : "archive",
											messageSection: "pollArchive"]
	}
	
	
}
