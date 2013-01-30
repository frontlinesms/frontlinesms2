package frontlinesms2.domain

import frontlinesms2.*
import spock.lang.*

class ForwardActionStepISpec extends grails.plugin.spock.IntegrationSpec {
	def "ForwardActionStep.recipient should return a list of addresses"() {
		given:
			def step = new ForwardActionStep(type: 'forward')
			if(addSentMessage) {
				step.addToStepProperties(new StepProperty(key:sentMessageText, value:"This is the message to forward"))
			}
			if(addStepProperty) {
				step.addToStepProperties(new StepProperty(key:stepPropertyKey, value:stepPropertyValue))
			}
		then:
			step.validate() == expectedOutcome
		where:
			addSentMessage | addStepProperty | stepPropertyKey | stepPropertyValue | expectedOutcome
			true           | false           | null            | "address-12341234"| false
			true           | true            | 'recipient'     | "address-12341234"| true
			false          | true            | 'recipient'     | "address-12341234"| false
	}
	
	def "ForwardActionStep should have at least one recipient"(){ }
}