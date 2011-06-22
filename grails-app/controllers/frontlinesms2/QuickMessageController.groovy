package frontlinesms2

class QuickMessageController {
	def create = {
		[contactList: Contact.list(), groupList: GroupMembership.getGroupDetails()]
	}
}