package frontlinesms2

class SearchController {
	def index = {
		redirect(action:'no_search')
	}
	
	def no_search = {
		[groupInstanceList : Group.findAll(),
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll()]
	}
	
	def result = {
		def groupInstance = params.groupId? Group.get(params.groupId): null
		def activityInstance = getActivityInstance()
		def messageOwners = activityInstance? getMessageOwners(activityInstance): null
		def searchResults = Fmessage.search(params.searchString, groupInstance, messageOwners)
		[searchDescription: getSearchDescription(params.searchString, groupInstance, activityInstance),,
				messageSection: 'search',
				searchString: params.searchString,
				groupInstance: groupInstance,
				activityId: params.activityId,
				messageInstanceList: searchResults,
				messageInstanceTotal: searchResults?.size()] << show(searchResults) << no_search()
	}
	
	def show = { searchResults ->
		def messageInstance = params.messageId ? Fmessage.get(params.messageId) :searchResults[0]
		if (messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		[messageInstance: messageInstance]
	}
	
	private def getSearchDescription(searchString, group, activity) {
		if(!searchString && !group && !activity) 'Start new search on the left'
		else {
			"Searching in " + {
				if(!activity && !group) {
					"all messages"
				} else if(!activity) {
					"'${group.name}'"
				} else {
					String activityDescription = activity instanceof Poll? activity.title: activity.name
					if(group) {
						"'${group.name}' and '$activityDescription'"
					} else "'$activityDescription'"
				}
			}()
		}
	}

	def downloadReport = {
			def groupInstance = params.groupId? Group.get(params.groupId): null
			def activityInstance = getActivityInstance()
			def messageOwners = activityInstance? getMessageOwners(activityInstance): null
			def messageInstanceList = Fmessage.search(params.searchString, groupInstance, messageOwners)
			messageInstanceList.sort { it.dateCreated }
			if(params.format == 'pdf')
				new ReportController().generatePDFReport(getSearchDescription(params.searchString, groupInstance, activityInstance), messageInstanceList)
			if(params.format == 'csv')
				new ReportController().generateCSVReport(getSearchDescription(params.searchString, groupInstance, activityInstance), messageInstanceList)
			
	}
	
	private def getActivityInstance() {
		if(!params.activityId) return null
		else {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'poll'? Poll: Folder
			def activityId = stringParts[1]
			activityType.findById(activityId)
		}
	}
	
	private def getMessageOwners(activity) {
		activity instanceof Poll ? activity.responses : [activity]
	}
}
