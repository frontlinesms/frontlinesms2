package frontlinesms2

class PollController {
	def index = {
		 redirect(action: "create", params: params)
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
}
