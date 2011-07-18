package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat


class SearchController {
	def exportService
	
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
	
	def deleteMessage = {
		withFmessage { messageInstance ->
			messageInstance.toDelete()
			messageInstance.save(failOnError: true, flush: true)
			Fmessage.get(params.messageId).messageOwner?.refresh()
			flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'message.label', default: 'Fmessage'), messageInstance.id])}"
			params.remove('messageId')
			redirect(action: result, params:params)
		}
	}
	
	def downloadReport = {
			def groupInstance = params.groupId? Group.get(params.groupId): null
			def activityInstance = getActivityInstance()
			def messageOwners = activityInstance? getMessageOwners(activityInstance): null
			def messageInstanceList 
			if(params.searchString) {messageInstanceList = Fmessage.search(params.searchString, groupInstance, messageOwners)}
			else {messageInstanceList = Fmessage.findAll()}
			messageInstanceList.sort { it.dateCreated }
			if(params.format == 'pdf')
				generatePDFReport(getSearchDescription(params.searchString, groupInstance, activityInstance), messageInstanceList)
			if(params.format == 'csv')
				generateCSVReport(getSearchDescription(params.searchString, groupInstance, activityInstance), messageInstanceList)
			
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
	
	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'poll'? Poll: Folder
			def activityId = stringParts[1]
			activityType.findById(activityId)
		} else return null
	}
	
	private def getMessageOwners(activity) {
		activity instanceof Poll ? activity.responses : [activity]
	}
	
	private def withFmessage(Closure c) {
		def m = Fmessage.get(params.messageId)
		if(m) c.call(m)
		else render(text: "Could not find message with id ${params.messageId}") // TODO handle error state properly
	}
	
	private def generateCSVReport(searchString, model) {
		def currentTime = new Date()
		List fields = ["id", "src", "dst", "text", "dateCreated"]
		Map labels = ["id":"DatabaseID", "src":"Source", "dst":"Destination", "text":"Text", "dateReceived":"Date"]
		Map parameters = [title: "$searchString"]
		def formatedTime = dateToString(currentTime)
		response.setHeader("Content-disposition", "attachment; filename=frontlineSMS-searchReport-${formatedTime}.csv")
		
		try{
			exportService.export(params.format, response.outputStream, model, fields, labels, [:],parameters)
		} catch(Exception e){
			render(text: "Error creating report")
		}
		
		[messageInstanceList: model]
	}
	
	private def generatePDFReport(searchString, model) {
		def currentTime = new Date()
		List fields = ["id", "src", "dst", "text", "dateCreated"]
		Map labels = ["id":"DatabaseID", "src":"Source", "dst":"Destination", "text":"Text", "dateReceived":"Date"]
		def formatedTime = dateToString(currentTime)
		Map parameters = [title: "$searchString"]
		response.setHeader("Content-disposition", "attachment; filename=frontlineSMS-searchReport-${formatedTime}.pdf")
		
		try{
			exportService.export(params.format, response.outputStream, model, fields, labels, [:],parameters)
		} catch(Exception e){
			render(text: "Error creating report")
		}
		
		[messageInstanceList: model]
	}

	private String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat()
		return formatedDate.format(date)
	}

	private DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy-MMM-dd", request.locale)
	}
}
