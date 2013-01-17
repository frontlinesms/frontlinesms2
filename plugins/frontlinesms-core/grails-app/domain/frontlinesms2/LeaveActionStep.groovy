package frontlinesms2

class LeaveActionStep extends Step {
	
	static service = 'subscription'
	static action = 'doLeave'
	static String getShortName() { 'leave' }

	static configFields = [group: Group]

	static constraints = {
		stepProperties nullable: true
	}
	
	def process(Fmessage message) {

	}
}
