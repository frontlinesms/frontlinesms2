package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

@TestFor(JoinActionStep)
@Mock([CustomActivity])
class JoinActionStepSpec extends Specification {
	@Unroll
	def "Test dynamic constraints"() {
		when:
			def step = new JoinActionStep(type: 'joinAction')
			step.activity = Mock(CustomActivity)
			if(addStepProperty)
				step.addToStepProperties(new StepProperty(key:stepPropertyKey, value:"invaluable"))
		then:
			step.validate() == expectedOutcome
		where:
			addStepProperty | stepPropertyKey | expectedOutcome
			true            | 'group'         | true
	}
}