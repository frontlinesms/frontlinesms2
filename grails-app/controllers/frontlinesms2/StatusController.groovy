package frontlinesms2

class StatusController {
	def index = {
		redirect action: "show", params:params
	}

	def trafficLightIndicator = {
		if(getSystemStatus(Fconnection.list()) == ConnectionStatus.CONNECTED) { render text:"green", contentType:'text/plain' }
		else { render text:"red", contentType:'text/plain' }
	}
	
	def show = {
		def fconnectionInstanceList = Fconnection.list()
		def fconnectionInstanceTotal = Fconnection.count()
		[connectionInstanceList: fconnectionInstanceList,
				fconnectionInstanceTotal: fconnectionInstanceTotal] <<
			getMessageStats() << getFilters()
	}
		
	private def getSystemStatus(allConnections) {
		println allConnections
		if(allConnections.any { it.getStatus() == "Not connected"}) return ConnectionStatus.NOT_CONNECTED
		else return ConnectionStatus.CONNECTED
	}

	private def getMessageStats() {
		def messageStatus = params.messageStatus
		def groupInstance = params.groupId? Group.get(params.groupId): null
		params.messageStatus = messageStatus ? messageStatus.tokenize(",")*.trim() : null
		def activityInstance = getActivityInstance()
		def messageOwners = activityInstance? Fmessage.getMessageOwners(activityInstance): null
		def startDate, endDate
		(startDate, endDate) = params.rangeOption == "between-dates" ? 
			[params.startDate, params.endDate] :
			[new Date() - 14, new Date()]
		params.groupInstance = groupInstance
		params.messageOwner = messageOwners
		params.startDate = startDate
		params.endDate = endDate
		def messageStats = Fmessage.getMessageStats(params)
		[messageStats: [xdata: messageStats.collect{k,v -> "'${k}'"}, 
						sent: messageStats.collect{k,v -> "${v["Sent"]}"},
						received: messageStats.collect{k,v -> "${v["Received"]}"} ], messageStatus: messageStatus]
	}
	
	private def getFilters() {
			def groupInstance = params.groupId? Group.get(params.groupId): null
			def activityInstance = getActivityInstance()
			def messageOwners = activityInstance? Fmessage.getMessageOwners(activityInstance): null
			params.rangeOption = params.rangeOption?: "two-weeks"
			[groupInstance: groupInstance,
					activityId: params.activityId,
					groupInstanceList : Group.findAll(),
					folderInstanceList: Folder.findAll(),
					pollInstanceList: Poll.findAll(), search:new Search(group:groupInstance, activityId:params.activityId)]
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
