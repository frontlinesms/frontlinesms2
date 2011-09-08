package frontlinesms2

class PollController {
	static allowedMethods = [update: "POST"]

	def index = {
		def archived = params['archived']
		[polls: Poll.findAllByArchived(archived),
		actionLayout : archived ? "archive" : "poll",
		messageSection: "poll"]
	}

	def rename = {
	}

	def update = {
		def poll = Poll.get(params.id)
		poll.properties = params
		poll.save()
		redirect(controller: "message", action: "poll", params: [ownerId: params.id])
	}

	def create = {
		[contactList: Contact.list(),
			groupList:Group.getGroupDetails()]
	}

	def save = {
		def pollInstance = Poll.createPoll(params)
		pollInstance.save()
		render ""
	}

	def archive = {
		def poll = Poll.get(params.id)
		poll.archived = true
		poll.save()
		flash['message'] = "Activity was archived successfully!"
		redirect(controller: "message", action: "inbox")
	}
}
