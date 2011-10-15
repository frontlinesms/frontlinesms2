package frontlinesms2

class PollController {
	
	static allowedMethods = [update: "POST"]

	def index = {
		[polls: Poll.findAllByArchivedAndDeleted(params.viewingArchive, false),
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
		forward(controller:"message", action:"send", params: params)
		render ""
	}

	def archive = {
		def poll = Poll.get(params.id)
		poll.archivePoll()
		poll.save()
		flash.message = "Poll was archived successfully!"
		redirect(controller: "message", action: "inbox")
	}
	
	def unarchive = {
		def poll = Poll.get(params.id)
		poll.unarchivePoll()
		poll.save()
		flash.message = "Poll was unarchived successfully!"
		redirect(controller: "archive", action: "pollView")
	}
	
	def confirmDelete = {
		def pollInstance = Poll.get(params.id)
		render view: 'confirmDelete', model: [pollInstance: pollInstance]
	}
	
	def delete = {
		def poll = Poll.get(params.id)
		poll.toDelete()
		poll.save()
		flash.message = "Poll has been trashed!"
		redirect(controller:"message", action:"inbox")
	}
}
