package frontlinesms2

class PollController {
	
	static allowedMethods = [update: "POST"]

	def index = {
		[polls: Poll.findAllByArchived(params.viewingArchive),
				actionLayout : params.viewingArchive ? "archive" : "poll",
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
		if(!params.enableKeyword) params.keyword = null
		
		def pollInstance = Poll.createPoll(params)
		pollInstance.save()
		render ""
	}

	def archive = {
		def poll = Poll.get(params.id)
		poll.archivePoll()
		poll.save()
		flash.message = "Poll was archived successfully!"
		redirect(controller: "message", action: "inbox")
	}
}
