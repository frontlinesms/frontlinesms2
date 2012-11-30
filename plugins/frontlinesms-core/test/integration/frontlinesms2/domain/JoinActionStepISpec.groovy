package frontlinesms2.domain

import frontlinesms2.*
import spock.lang.*

class JoinActionStepISpec extends grails.plugin.spock.IntegrationSpec {
	@Unroll
	def "Test dynamic constraints"() {
		when:
			def step = new JoinActionStep(type: 'joinAction')
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
