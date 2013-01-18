package frontlinesms2

class LeaveActionStep extends Step {
	def subscriptionService
	static service = 'subscription'
	static action = 'doLeave'
	static String getShortName() { 'leave' }

	static configFields = [group: Group]

	static constraints = {
		stepProperties nullable: true
	}

	Map getConfig() {
		[stepId:id, groupId:getGroupId()]
	}
	
	def getGroup() {
		Group.get(getPropertyValue("group"))	
	}

        def getGroupId() {
                getPropertyValue("group")
        }

	def setGroup(Group group) {
		setPropertyValue("group", group.id)
	}
	
	def process(Fmessage message) {
		subscriptionService.doLeave(this, message)
	}

	def getNiceFormat() {
		"Leaving '${this?.group?.name}' group"
        }

}
