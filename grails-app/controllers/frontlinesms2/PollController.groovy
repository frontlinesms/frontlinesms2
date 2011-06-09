package frontlinesms2

class PollController {
	def index = {
		 redirect(action: "create", params: params)
	}

	def create = {
		def pollInstance = new Poll()
		pollInstance.properties = params
		[pollInstance: pollInstance]
	}

	def save = {
		def responseList = params.responses.tokenize()
		def pollInstance = Poll.createPoll(params.title, responseList)
		
		if (pollInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.poll', args: [message(code: 'poll.label', default: 'Poll'), pollInstance.id])}"
			redirect(controller: "message")
		} else {
			println "Something went wrong while saving the poll instance."
			render(view: "create", model: [pollInstance: pollInstance])
		}
	}
}
