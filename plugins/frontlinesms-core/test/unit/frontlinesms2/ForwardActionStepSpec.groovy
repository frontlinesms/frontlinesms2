package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import grails.buildtestdata.mixin.Build

@Mock([CustomActivity, Fmessage, Group, MessageSendService])
@TestFor(ForwardActionStep)
class ForwardActionStepSpec extends Specification {
	@Unroll
	def "ForwardActionStep should have at least one recipient"() {
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
			true           | false           | null            | "Address-12341234"| false
			true           | true            | 'recipient'     | "Address-12341234"| true
			false          | true            | 'recipient'     | "Address-12341234"| false
	}

	def "ForwardActionStep.process should call autoforwardService.doForward"(){
		given:
			def forwardActionStep = new ForwardActionStep()
						.addToStepProperties(new StepProperty(key:"sentMessageText", value:"some forward text"))
						.addToStepProperties(new StepProperty(key:"recipient", value:"Address-12341234"))
			def forwardService = Mock(AutoforwardService)
			def message =  Mock(Fmessage)
			forwardActionStep.autoforwardService = forwardService
		when:
			forwardActionStep.process(message)
		then:
			1 * forwardService.doForward(forwardActionStep, message)
	}

	private createMockOutgoingMessage() {
		def m = Mock(Fmessage)
		m.id >> 1
		m.setOwnerDetail(_,_) >> "setting the owner detail"
		return m
	}
}
