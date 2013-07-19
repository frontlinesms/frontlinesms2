package frontlinesms2

class ArchiveController extends MessageController {
	def index() {
		def action = params.messageSection ?: 'inbox'
		redirect action:action, params:params
	}
	
	def activityList() {
		def activityInstanceList = Activity.findAllByArchivedAndDeleted(true, false)
		render view:'../message/standard', model:[activityInstanceList: activityInstanceList,
				activityInstanceTotal: activityInstanceList.size(),
				messageSection: "activity"]
	}
	
	def folderList() {
		def folderInstanceList = Folder.findAllByArchivedAndDeleted(true, false)
		render view:'../message/standard', model:[folderInstanceList: folderInstanceList,
				itemInstanceTotal: folderInstanceList.size(),
				messageSection: "folder"]
	}
}

