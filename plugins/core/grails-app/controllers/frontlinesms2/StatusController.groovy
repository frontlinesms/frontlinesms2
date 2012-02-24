package frontlinesms2

import frontlinesms2.RouteStatus

class StatusController {
	def deviceDetectionService
	
	def index = {
		redirect action: "show", params:params
	}

	def trafficLightIndicator = {
		def connections = SmslibFconnection.list() + EmailFconnection.list()
		def color = !connections || connections.status.any { it.toString() == "Not connected" } ? 'red': 'green'
		render text:color, contentType:'text/plain'
	}
	
	def show = {
		[connectionInstanceList: SmslibFconnection.list() + EmailFconnection.list(),
				connectionInstanceTotal: Fconnection.count(),
				detectedDevices:deviceDetectionService.detected] <<
			getMessageStats() << getFilters()
	}
	
	def detectDevices = {
		deviceDetectionService.detect()
		redirect action:'show'
	}
	
	def listDetected = {
		render template:'device_detection', plugin:'core', model:[detectedDevices:deviceDetectionService.detected]
	}
	
	def resetDetection = {
		deviceDetectionService.reset()
		redirect action:'index'
	}

	private def getMessageStats() {
		def activityInstance = getActivityInstance()
		params.startDate = params.rangeOption == "between-dates" ? params.startDate : new Date() - 14
		params.endDate = params.rangeOption == "between-dates" ? params.endDate : new Date()
		params.messageOwner = (activityInstance && activityInstance instanceof Poll) ? activityInstance.responses : activityInstance
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
					folderInstanceList: Folder.findAll(),
					pollInstanceList: Poll.findAll(),
					search: new Search(group:groupInstance,	activityId: params.activityId)]
	}
	
	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'poll' ? Poll : Folder
			def activityId = stringParts[1]
			activityType.findById(activityId)
		} else return null
	}
}
