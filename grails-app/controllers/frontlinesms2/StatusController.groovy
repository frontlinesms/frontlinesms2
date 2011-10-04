package frontlinesms2

class StatusController {
	def deviceDetectionService
	
	def index = {
		redirect action: "show", params:params
	}

	def trafficLightIndicator = {
		def connections = Fconnection.list()
		def color = !connections || connections*.status.any { it == RouteStatus.NOT_CONNECTED } ? 'red': 'green'
		render text:color, contentType:'text/plain'
	}
	
	def show = {
		def fconnectionInstanceList = Fconnection.list()
		def fconnectionInstanceTotal = Fconnection.count()
		[connectionInstanceList: fconnectionInstanceList,
				fconnectionInstanceTotal: fconnectionInstanceTotal,
				detectedDevices:deviceDetectionService.detected] <<
			getMessageStats() << getFilters()
	}
	
	def detectDevices = {
		deviceDetectionService.detect()
		redirect action:'show'
	}
	
	def listDetected = {
		render template:'device_detection', model:[detectedDevices:deviceDetectionService.detected]
	}
	
	def resetDetection = {
		deviceDetectionService.reset()
		redirect action:'index'
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
