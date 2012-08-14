package frontlinesms2

class SubscriptionController {

	def create = {
		def groupList = Group.getAll()
		[contactList: Contact.list(),
				groupList:groupList]
	}
}
