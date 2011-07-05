package frontlinesms2

class SubscriptionController {
	def create = {
		['groups' : Group.list()]
	}
}