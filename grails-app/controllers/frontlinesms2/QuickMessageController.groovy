package frontlinesms2

class QuickMessageController {
	def create = {
		def recipients = params['recipients'] ? [params['recipients']].flatten() : []
		def fowardMessage = params['messageText'] ? params['messageText'] : []
		def contacts = Contact.list()
		def configureTabs = params['configureTabs'] ?: ['tabs-1', 'tabs-2', 'tabs-3']
		[contactList: contacts,
			configureTabs: configureTabs,
			groupList:Group.getGroupDetails(),
			recipients:recipients,
			messageText: fowardMessage,
			nonExistingRecipients:recipients - contacts*.getPrimaryMobile() - contacts*.getSecondaryMobile() - contacts*.getEmail()]
	}
}
