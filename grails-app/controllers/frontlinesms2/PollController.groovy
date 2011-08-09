package frontlinesms2

class PollController {
	def index = {
		def archived = params['archived']
		[polls: Poll.findAllByArchived(archived),
		actionLayout : archived ? "archive" : "poll",
		messageSection: "poll"]
	}

	def create = {
		[contactList: Contact.list(),
			groupList:Group.getGroupDetails()]
	}

	def save = {
		def pollInstance = Poll.createPoll(params)
		
		if (pollInstance.validate()) {
			pollInstance.save()
			flash.message = "${message(code: 'default.created.poll', args: [message(code: 'poll.label', default: 'Poll'), pollInstance.id])}"
			redirect(controller: "message", action:'inbox', params:[flashMessage: flash.message])
		} else {
			flash.message = "error"
			redirect(controller: "message", action:'inbox', params:[flashMessage: flash.message])
		}
	}

	def archive = {
		def poll = Poll.get(params.id)
		poll.archived = true
		poll.save()
		redirect(controller: "message")
	}
}
