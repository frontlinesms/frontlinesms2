package frontlinesms2

class ForwardActionStep extends Step {
	def autoforwardService
	static service = 'autoforward'
	static action = 'doForward'
	static String getShortName() { 'forward' }

	static configFields = [sentMessageText: 'textarea', recipients: '']

	static constraints = {
	}

	Map getConfig() {
                [stepId:id, sentMessageText:sentMessageText]
        }

	def getSentMessageText() {
		getPropertyValue("sentMessageText")
	}

	def getRecipients() {
		//TODO implement parsing of stepProperties so as to get the recipient lists
		return []
	}

	def setRecipients(groups, smartGroups, contacts, addresses) { }
	
	def process(Fmessage message) {
		autoforwardService.doForward(this, message)
	}

	def getNiceFormat() {
		"Forwarding with '${this.sentMessageText}'"
	}

}
