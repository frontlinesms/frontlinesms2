package frontlinesms2

class ReplyActionStep extends Step {
	
	static service = 'autoreply'
	static action = 'doReply'
	static configFields = [autoreplyText: 'textarea']

	static constraints = {
	}
	
	def process(Fmessage message) {

	}
}
