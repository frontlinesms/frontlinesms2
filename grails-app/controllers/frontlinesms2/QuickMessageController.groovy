package frontlinesms2

class QuickMessageController {
	def create = {
		def recipients = [params['recipient']]
		def contacts = Contact.list()
		println contacts
		println recipients

		[contactList: contacts,
			groupList:GroupMembership.getGroupDetails(),
			recipients:recipients,
			nonExistingRecipients:recipients - (recipients.intersect(contacts*.getAddress()))]

	}
}