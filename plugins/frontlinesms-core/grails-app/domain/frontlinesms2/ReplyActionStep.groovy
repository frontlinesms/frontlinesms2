package frontlinesms2

class ReplyActionStep extends Step {
	def autoreplyService
	static service = 'autoreply'
	static action = 'doReply'
	static String getShortName() { 'reply' }

	static configFields = [autoreplyText: 'textarea']

	static constraints = {
	}

	Map getConfig() {
		[stepId:id, autoreplyText:autoreplyText]
	}

	def getAutoreplyText() {
		getPropertyValue("autoreplyText")
	}
	
	def process(Fmessage message) {
		autoreplyService.doReply(this, message)
	}

		def getNiceFormat() {
		"Replying with '${this.autoreplyText}'"
	}

}
