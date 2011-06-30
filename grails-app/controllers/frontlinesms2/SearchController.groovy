package frontlinesms2

class SearchController {
	def index = {
		def groupInstance = params.groupId? Group.get(params.groupId): null
		def activityInstance = getActivityInstance()
		def messageOwners = activityInstance? getMessageOwners(activityInstance): null
		def results = Fmessage.search(params.searchString, groupInstance, messageOwners)
		[searchDescription: getSearchDescription(params.searchString, groupInstance, activityInstance),
				groupInstanceList : Group.findAll(),
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll(),
				searchString: params.searchString,
				groupInstance: groupInstance,
				activityInstance: activityInstance,
				activityId: params.activityId,
				messageInstanceList: results,
				messageInstanceTotal: results?.size()]
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
			println "params at controller: $params.params"
			def groupInstance = params.groupId? Group.get(params.groupId): null
			def activityInstance = getActivityInstance()
			def messageOwners = activityInstance? getMessageOwners(activityInstance): null
			def messageInstanceList = Fmessage.search(params.searchString, groupInstance, messageOwners)
			println "List at search: ${messageInstanceList.class}"
			if(params.format == 'pdf')
				new ReportController().generatePDFReport(messageInstanceList)
			if(params.format == 'csv')
				new ReportController().generateCSVReport(messageInstanceList)
			
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
