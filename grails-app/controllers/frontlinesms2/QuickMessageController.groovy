package frontlinesms2

class QuickMessageController {
	def create = {
		def recipients = params['recipient'] ? [params['recipient']] : []
		def contacts = Contact.list()

		[contactList: contacts,
			groupList:Group.getGroupDetails(),
			recipients:recipients,
			nonExistingRecipients:recipients - (recipients.intersect(contacts*.getPrimaryMobile()))]

	}
}