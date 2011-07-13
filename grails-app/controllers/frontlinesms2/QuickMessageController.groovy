package frontlinesms2

class QuickMessageController {
	def create = {
		def recipients = params['recipient'] ? [params['recipient']] : []
		def fowardMessage = params['messageText'] ? params['messageText'] : []
		def contacts = Contact.list()
		println params
		[contactList: contacts,
			groupList:Group.getGroupDetails(),
			recipients:recipients,
			messageText: fowardMessage,
			nonExistingRecipients:recipients - (recipients.intersect(contacts*.getPrimaryMobile())) - (recipients.intersect(contacts*.getSecondaryMobile())) - (recipients.intersect(contacts*.getEmail()))]

	}
}