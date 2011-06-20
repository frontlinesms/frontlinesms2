package frontlinesms2

class SearchController {
	def index = {
		redirect(action: "list", params: params)
	}
	
	def list = {
		[groupInstanceList : Group.findAll(),
			folderInstanceList: Folder.findAll(),
			pollInstanceList: Poll.findAll()]
	}
	
	def search = {
		println "Action:search; params:$params"
		def groupInstance = Group.get(params.groupId)
		def activityInstance = getActivityInstance()
		def messageOwner = getMessageOwner(activityInstance)
		 
		def results = search(params.keywords, groupInstance, messageOwner)
		def latestMessage
		if(results){
			render(view:'list', model:list()<<[keywords: params.keywords, groupInstance: groupInstance, activityInstance: activityInstance, messageInstanceList: results, messageInstanceTotal: results.size()])
		} else {
			render(view:'list', model:list()<<[keywords: params.keywords, groupInstance: groupInstance, activityInstance: activityInstance])
		}	
	}
	
	def search(keywords, groupInstance, messageOwner) {
		def results
		def groupContactAddresses = groupInstance?.getMembers()*.address
		if(!groupContactAddresses){
			groupContactAddresses = "null"
		}
		if(keywords){
			results =Fmessage.createCriteria().list {
				like("text","%${params.keywords}%")
				and{
					if(groupInstance){
						'in'("src",  groupContactAddresses)
					}
					if(messageOwner){
						'in'("messageOwner", messageOwner)
					}
					eq('deleted', false)
				}
				order('dateRecieved', 'desc')
				order('dateCreated', 'desc')
			}
		}
		results*.updateDisplaySrc()
		results
	}

	private def getActivityInstance() {
		if(!params.activityId) return null
		else {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'poll'? Poll : Folder
			println "Activity type: $activityType"
			def activityId = stringParts[1]
			println "activityId: $activityId"
			def activity = activityType.findById(activityId)
			println "Fetched activity: $activity"
			activity
		}
	}
	
	private def getMessageOwner(activity) {
		activity instanceof Poll ? activity.responses : activity
	}
}