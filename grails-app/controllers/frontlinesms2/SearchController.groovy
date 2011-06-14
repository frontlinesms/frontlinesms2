package frontlinesms2

class SearchController {
	def index = {
		redirect(action: "list", params: params)
	}
	
	def list = {
		def messageInstance = Fmessage.get(params.id)
		[messageInstance: messageInstance,
			groupInstanceList : Group.findAll(),
			pollInstanceList: Poll.findAll()]
	}
	
	def search = {
		def groupInstance = Group.get(params.groupList)
		def pollInstance = Poll.get(params?.pollList)
		def results = search(params.keywords, groupInstance, pollInstance)
		def latestMessage
		if(results){
			results.each {
			if(!latestMessage) {
				latestMessage = it
			} else{
				if(it.dateCreated.compareTo(latestMessage.dateCreated) < 0) {
					latestMessage = it
				}
			}
		}
		
		params.id = latestMessage?.id
			render(view:'list', model:list()<<[keywords: params.keywords, groupInstance: groupInstance, pollInstance: pollInstance, messageInstanceList: results, messageInstanceTotal: results.size()])
		} else {
			render(view:'list', model:list()<<[keywords: params.keywords, groupInstance: groupInstance, pollInstance: pollInstance])
		}	
	}
	
	def search(keywords, groupInstance, pollInstance) {
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
					if(pollInstance){
						'in'("activity", pollInstance.responses)
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
	
}