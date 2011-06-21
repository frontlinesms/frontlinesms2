package frontlinesms2

class QuickMessageController {
	def create = {
		[contactList: Contact.list(), groupList: Group.list()]
	}
}