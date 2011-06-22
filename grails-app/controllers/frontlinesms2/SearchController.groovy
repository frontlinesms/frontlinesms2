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
		def groupInstance = Group.get(params.groupList)
		def messageOwner
		def activityInstance
		if(params.activityList){
			if(Poll.findById(params.activityList) && Poll.findByTitle(params.selectedActivity)){
				activityInstance = Poll.findById(params.activityList)
				messageOwner = activityInstance.responses
			}
			else if(Folder.findById(params.activityList) && Folder.findByValue(params.selectedActivity)){
				activityInstance = Folder.findById(params.activityList)
				messageOwner = Folder.findById(params.activityList)
			}
		}
		 
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
				order('dateReceived', 'desc')
				order('dateCreated', 'desc')
			}
		}
		results*.updateDisplaySrc()
		results
	}
	
}