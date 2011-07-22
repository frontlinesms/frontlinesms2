package frontlinesms2

class QuickMessageController {
	def create = {
		def configureTabs = params['configureTabs'] ?: ['tabs-1', 'tabs-2', 'tabs-3']
		def recipients = params['recipient'] ? [params['recipient']] : []
		def fowardMessage = params['messageText'] ? params['messageText'] : []
		def contacts = Contact.list()
		[contactList: contacts,
			configureTabs: configureTabs,
			groupList:Group.getGroupDetails(),
			recipients:recipients,
			messageText: fowardMessage,
			nonExistingRecipients:recipients - (recipients.intersect(contacts*.getPrimaryMobile())) - (recipients.intersect(contacts*.getSecondaryMobile())) - (recipients.intersect(contacts*.getEmail()))]

	}
}