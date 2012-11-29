package frontlinesms2

import spock.lang.*
import grails.test.mixin.*
import grails.buildtestdata.mixin.Build

@TestFor(JoinActionStep)
@Mock(Fmessage)
@Build(StepProperty)
class JoinActionStepSpec extends Specification {

	def "Test dynamic constraints"() {
		when:
			def step = new JoinActionStep()
			if(addStepProperty)
				step.addToStepProperties(StepProperty.build(key:stepPropertyKey))
		then:
			step.validate() == expectedOutcome
		where:
			addStepProperty | stepPropertyKey | expectedOutcome
			false           | null            | false
			true            | "woteva"        | false
			true            | "group"         | true
	}
}
