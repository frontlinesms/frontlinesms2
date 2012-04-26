package frontlinesms2

class ArchiveController extends MessageController {
//> SERVICES

//> INTERCEPTORS
	 
//> ACTIONS
	def index = {
		params.sort = 'date'
		def action = params.messageSection ?: 'inbox'
		println "index: $params"
		redirect(action:action, params:params)
	}
	
	def activityList = {
		def activityInstanceList = Activity.findAllByArchivedAndDeleted(true, false)
		render view:'../message/standard', model:[activityInstanceList: activityInstanceList,
				activityInstanceTotal: activityInstanceList.size(),
				messageSection: "activity"]
	}
	
	def folderList = {
		def folderInstanceList = Folder.findAllByArchivedAndDeleted(true, false)
		render view:'../message/standard', model:[folderInstanceList: folderInstanceList,
				itemInstanceTotal: folderInstanceList.size(),
				messageSection: "folder"]
	}
	
//> PRIVATE HELPERS
	private def getShowModel(messageInstanceList) {
		def model = super.getShowModel(messageInstanceList)
		return model
	}
}
