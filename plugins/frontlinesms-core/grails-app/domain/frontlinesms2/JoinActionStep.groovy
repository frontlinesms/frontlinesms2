package frontlinesms2

class JoinActionStep extends Step {
	
	static hasMany = [stepProperties: StepProperty]
	static service = 'subscription'
	static action = 'doJoin'
	static configFields = [group: Group]

	static constraints = {
	}
	
	def process(Fmessage message) {

	}
}
