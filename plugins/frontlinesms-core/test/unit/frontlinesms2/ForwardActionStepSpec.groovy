package frontlinesms2

import spock.lang.*

@Mock([CustomActivity, TextMessage, Group, MessageSendService])
@TestFor(ForwardActionStep)
class ForwardActionStepSpec extends Specification {
	@Unroll
	def "can create a ForwardActionStep"() {
		given:
			def step = new ForwardActionStep(type: 'forward', activity:Mock(CustomActivity))
			if(addSentMessage) {
				step.addToStepProperties(new StepProperty(key:'sentMessageText', value:"This is the message to forward"))
			}
			if(addStepProperty) {
				step.addToStepProperties(new StepProperty(key:stepPropertyKey, value:stepPropertyValue))
			}
		expect:
			step.validate() == expectedOutcome
			println "## ## ${step.errors}"
		where:
			addSentMessage | addStepProperty | stepPropertyKey | stepPropertyValue | expectedOutcome
			true           | true            | 'recipient'     | "Address-12341234"| true
	}

	def "ForwardActionStep.process should call autoforwardService.doForward"(){
		given:
			def forwardActionStep = new ForwardActionStep()
						.addToStepProperties(new StepProperty(key:"sentMessageText", value:"some forward text"))
						.addToStepProperties(new StepProperty(key:"recipient", value:"Address-12341234"))
			def forwardService = Mock(AutoforwardService)
			def message =  Mock(TextMessage)
			forwardActionStep.autoforwardService = forwardService
		when:
			forwardActionStep.process(message)
		then:
			1 * forwardService.doForward(forwardActionStep, message)
	}
}

