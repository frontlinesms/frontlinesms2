package frontlinesms2

class StatusController {
	def index = {
		redirect action: "show", params:params
	}

	def trafficLightIndicator = {
		render text:getStatus(fetchAllStatus()).getIndicator(), contentType:'text/plain'
	}
	
	def show = {
		fetchAllStatus() << getMessageStats() << getFilters()
	}
		
	private def getStatus(allStatus) {
		if(fetchAllStatus().any { it.value == ConnectionStatus.NOT_CONNECTED}) return ConnectionStatus.NOT_CONNECTED
		else if(fetchAllStatus().any { it.value ==  ConnectionStatus.ERROR}) return ConnectionStatus.ERROR
		else return ConnectionStatus.CONNECTED
	}

	//FIXME: This is a stub method.
	private def fetchAllStatus() {
		['MTNDONGLE' : ConnectionStatus.ERROR, "GMAIL": ConnectionStatus.CONNECTED, "INTERNET": ConnectionStatus.CONNECTED,
			"MESSAGEQUEUE": ConnectionStatus.CONNECTED]
	}
	
	private def getMessageStats() {
		def groupInstance = params.groupId? Group.get(params.groupId): null
		def activityInstance = getActivityInstance()
		def messageOwners = activityInstance? Fmessage.getMessageOwners(activityInstance): null
		def startDate, endDate
		(startDate, endDate) = params.rangeOption == "between-dates" ? 
			[params.startDate, params.endDate] :
			[new Date() - 14, new Date()]
		println "${startDate} ${endDate}"
		def messageStats = Fmessage.getMessageStats(groupInstance, messageOwners, startDate, endDate)
		[messageStats: [xdata: messageStats.collect{k,v -> "'${k}'"}, 
						sent: messageStats.collect{k,v -> "${v["Sent"]}"},
						received: messageStats.collect{k,v -> "${v["Received"]}"} ]]
	}
	
	private def getFilters() {
			def groupInstance = params.groupId? Group.get(params.groupId): null
			def activityInstance = getActivityInstance()
			def messageOwners = activityInstance? Fmessage.getMessageOwners(activityInstance): null		
			[groupInstance: groupInstance,
					activityId: params.activityId,
					groupInstanceList : Group.findAll(),
					folderInstanceList: Folder.findAll(),
					pollInstanceList: Poll.findAll()]
	}
	
	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'poll'? Poll: Folder
			def activityId = stringParts[1]
			activityType.findById(activityId)
		} else return null
	}
}