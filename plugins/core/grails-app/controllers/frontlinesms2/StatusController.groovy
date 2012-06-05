package frontlinesms2

class StatusController {
	def deviceDetectionService
	
	def index = {
	    
		redirect action: "show", params:params
	}

	def trafficLightIndicator = {
		def connections = Fconnection.list()
		def color = (connections && connections.status.any {(it == RouteStatus.CONNECTED)}) ? 'green' : 'red'
		render text:color, contentType:'text/plain'
	}
	
	def show = {
		[connectionInstanceList: Fconnection.list(),
				connectionInstanceTotal: Fconnection.count(),
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
		def activityInstance = MessageOwner.get(params.activityId)
		params.startDate = params.rangeOption == "between-dates" ? new Date(params.startDate) : new Date() - 14
		params.endDate = params.rangeOption == "between-dates" ? new Date(params.endDate) : new Date()
		params.messageOwner = activityInstance
		params.groupInstance = params.groupId ? Group.get(params.groupId) : null
		params.messageStatus = params.messageStatus?.tokenize(",")*.trim()
		def messageStats = Fmessage.getMessageStats(params) // TODO consider changing the output of this method to match the data we actually want
		[messageStats: [xdata: messageStats.keySet().collect{"'${it}'"},
							sent: messageStats.values()*.sent,
							received: messageStats.values()*.received]]
	}
	
	private def getFilters() {
			def groupInstance = params.groupId? Group.get(params.groupId): null
			params.rangeOption = params.rangeOption ?: "two-weeks"
			[groupInstance: groupInstance,
					activityId: params.activityId,
					groupInstanceList : Group.findAll(),
					activityInstanceList: Activity.findAll(),
					folderInstanceList: Folder.findAll(),
					search: new Search(group:groupInstance,	activityId: params.activityId)]
	}
	
}
