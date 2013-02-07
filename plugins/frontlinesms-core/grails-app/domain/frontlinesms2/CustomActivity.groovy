package frontlinesms2

class CustomActivity extends Activity {
	List steps
	def customActivityService
	static String getShortName() { 'customactivity' }
	static hasMany = [steps: Step]

	def getActivityMessages(getOnlyStarred=false, getSent=null, stepId=null, params=null) {
		if(stepId) {
			def outgoingMessagesByStep = []
			if((Step.get(stepId) instanceof ReplyActionStep) || (Step.get(stepId) instanceof ForwardActionStep)) {
				outgoingMessagesByStep = MessageDetail.findAllByOwnerTypeAndOwnerId(MessageDetail.OwnerType.STEP, stepId).collect{ it.message }
			}
			return (outgoingMessagesByStep + Fmessage.owned(this, getOnlyStarred, true)?.list(params?:[:])).flatten()
		} else {
			Fmessage.owned(this, getOnlyStarred, getSent).list(params?:[:])
		}
	}

	def processKeyword(Fmessage message, Keyword matchedKeyword) {
		addToMessages(message)
		save(flush:true)
		customActivityService.triggerSteps(this, message)
	}

	def activate() {
		steps.each { it.activate() }
	}

	def deactivate() {
		steps.each { it.deactivate() }
	}
}

