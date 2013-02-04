package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

@TestFor(LeaveActionStep)
@Mock([CustomActivity])
class LeaveActionStepSpec extends Specification {
	@Unroll
	def "Test dynamic constraints"() {
		when:
			def step = new LeaveActionStep(type: 'leaveAction')
			step.activity = Mock(CustomActivity)
			if(addStepProperty)
				step.addToStepProperties(new StepProperty(key:stepPropertyKey, value:"invaluable"))
		then:
			step.validate() == expectedOutcome
		where:
			addStepProperty | stepPropertyKey | expectedOutcome
			false           | null            | false
			true            | 'woteva'        | false
			true            | 'group'         | true
	}
}
