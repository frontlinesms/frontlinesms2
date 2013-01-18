package frontlinesms2

class ReplyActionStep extends Step {
	def autoreplyService
	static service = 'autoreply'
	static action = 'doReply'
	static String getShortName() { 'reply' }

	static configFields = [autoreplyText: 'textarea']

	static constraints = {
	}

<<<<<<< HEAD
	Map getConfig() {
		[stepId:id, autoreplyText:autoreplyText]
	}

=======
>>>>>>> 6a97f05... Merge branch 'CORE-1104' of github.com:frontlinesms/frontlinesms2 into CORE-1104
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
