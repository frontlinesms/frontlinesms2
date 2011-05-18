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
        def pollInstance = new Poll(params)
		if (pollInstance.save(flush: true)
			) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'poll.label', default: 'Poll'), pollInstance.id])}"
			redirect(controller: "message")
		} else {
			println "Something went wrong while saving the poll instance."
            render(view: "create", model: [pollInstance: pollInstance])
        }
    }
}
