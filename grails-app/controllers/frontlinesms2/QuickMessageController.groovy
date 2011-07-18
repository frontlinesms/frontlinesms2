package frontlinesms2

class QuickMessageController {
	def create = {
	
		def recipients = params['recipient'] ? [params['recipient']] : []
		def fowardMessage = params['messageText'] ? params['messageText'] : []
		def contacts = Contact.list()
		def checkedMessageSrcList;
		
		if(params.checkedMessageIds) {
			checkedMessageSrcList = getCheckedMessageSrcList()
			recipients.addAll(checkedMessageSrcList)
		}
		
		[contactList: contacts,
			groupList:Group.getGroupDetails(),
			recipients:recipients,
			messageText: fowardMessage,
			nonExistingRecipients:recipients - (recipients.intersect(contacts*.getPrimaryMobile())) - (recipients.intersect(contacts*.getSecondaryMobile())) - (recipients.intersect(contacts*.getEmail()))]

	}
	
	private def getCheckedMessageSrcList() {
		def messageSrcList = []
		def checkedMessageIds = params.checkedMessageIds.tokenize(',').unique();
		checkedMessageIds.each { id ->
			messageSrcList << Fmessage.get(id).src
		}
		messageSrcList	
	}
}
