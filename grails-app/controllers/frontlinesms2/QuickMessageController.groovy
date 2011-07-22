package frontlinesms2

class QuickMessageController {
	def create = {
	
		def recipients = params['recipient'] ? [params['recipient']] : []
		def fowardMessage = params['messageText'] ? params['messageText'] : []
		def contacts = Contact.list()
		def checkedMessageSrcList;
		
		if(params.checkedMessageIdList) {
			checkedMessageSrcList = getCheckedMessageSrcList()
			recipients.addAll(checkedMessageSrcList)
		}
		params.remove('checkedMessageIdList')

		[contactList: contacts,
			groupList:Group.getGroupDetails(),
			recipients:recipients,
			messageText: fowardMessage,
			nonExistingRecipients:recipients - (recipients.intersect(contacts*.getPrimaryMobile())) - (recipients.intersect(contacts*.getSecondaryMobile())) - (recipients.intersect(contacts*.getEmail()))]

	}
	
	private def getCheckedMessageSrcList() {
		def messageSrcList = []
		def checkedMessageIdList = params.checkedMessageIdList.tokenize(',').unique();
		checkedMessageIdList.each { id ->
			messageSrcList << Fmessage.get(id).src
		}
		messageSrcList	
	}
}
