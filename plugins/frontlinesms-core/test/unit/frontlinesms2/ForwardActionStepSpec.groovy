package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import grails.buildtestdata.mixin.Build

@Mock([CustomActivity])
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
}