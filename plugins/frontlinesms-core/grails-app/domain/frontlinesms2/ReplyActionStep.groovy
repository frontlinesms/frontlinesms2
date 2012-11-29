package frontlinesms2

class ReplyActionStep extends Step {
	String type
	static hasMany = [stepProperties: StepProperty]
	static service = 'autoreply'
	static action = 'doReply'
	static configFields = [message: 'textarea']

	static constraints = {
		stepProperties nullable: true
	}
	
	def process(Fmessage message) {

	}
}
