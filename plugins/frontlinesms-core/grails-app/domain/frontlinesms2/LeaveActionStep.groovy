package frontlinesms2

class LeaveActionStep extends Step {
	String type
	static hasMany = [stepProperties: StepProperty]
	static service = 'subscription'
	static action = 'doLeave'
	static configFields = [group: Group]

	static constraints = {
		stepProperties nullable: true
	}
	
	def process(Fmessage message) {

	}
}
