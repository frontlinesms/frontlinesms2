package frontlinesms2

class JoinActionStep extends Step {
	String type
	static hasMany = [stepProperties: StepProperty]
	static service = 'subscription'
	static action = 'doJoin'
	static configFields = [group: Group]

	static constraints = {
	}

	def getGroup() {
		Group.get(getPropertyValue("group"))	
	}

	def setGroup(Group group) {
		setPropertyValue("group", group.id)
	}

	def process(Fmessage message) {

	}
}
