package frontlinesms2

class CustomActivity extends Activity {
	List steps
	def customActivityService
	static String getShortName() { 'customactivity' }
	static hasMany = [steps: Step]

	static mapping = {
		steps cascade: "all-delete-orphan"
	}

	def getActivityMessages(getOnlyStarred=false, getSent=null, stepId=null, params=null) {
		if(stepId) {
			def outgoingMessagesByStep = []
			if((Step.get(stepId) instanceof ReplyActionStep) || (Step.get(stepId) instanceof ForwardActionStep)) {
				outgoingMessagesByStep = MessageDetail.findAllByOwnerTypeAndOwnerId(MessageDetail.OwnerType.STEP, stepId).collect{ it.message }
			}
			return (outgoingMessagesByStep + TextMessage.owned(this, getOnlyStarred, true)?.list(params?:[:])).flatten()
		} else {
			TextMessage.owned(this, getOnlyStarred, getSent).list(params?:[:])
		}
	}

	def processKeyword(TextMessage message, Keyword matchedKeyword) {
		this.addToMessages(message)
		this.save(flush:true)
		customActivityService.triggerSteps(this, message)
	}

	def activate() {
		steps.each { it.activate() }
	}

	def deactivate() {
		steps.each { it.deactivate() }
	}
}

