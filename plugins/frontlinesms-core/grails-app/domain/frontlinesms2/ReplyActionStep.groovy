package frontlinesms2

class ReplyActionStep extends Step {
	
	static service = 'autoreply'
	static action = 'doReply'
	static String getShortName() { 'reply' }

	static configFields = [autoreplyText: 'textarea']

	static constraints = {
	}

	def getAutoreplyText() {
		getPropertyValue("autoreplyText")
	}
	
	def process(Fmessage message) {

	}
}
