package frontlinesms2

import grails.util.GrailsConfig

class ArchiveController extends MessageController {
	def beforeInterceptor = {
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		params.offset  = params.offset ?: 0
		params.viewingArchive = true
		params.viewingMessages = params.viewingMessages ? params.viewingMessages.toBoolean() : false
		true
	}
	
	def index = {
		redirect(action:'inbox')
	}
	
	def pollView = {
		def pollInstanceList = Poll.findAllByArchived(true)
		render view:'standard', model:[pollInstanceList: pollInstanceList,
											itemInstanceTotal: pollInstanceList.size(),
											messageSection: "poll"]
	}
	
	def folderView = {
		def folderInstanceList = Folder.findAllByArchived(true)
		render view:'standard', model:[folderInstanceList: folderInstanceList,
											itemInstanceTotal: folderInstanceList.size(),
											messageSection: "folder"]
	}
}
