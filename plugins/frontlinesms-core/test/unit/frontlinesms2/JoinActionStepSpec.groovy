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
			def step = new JoinActionStep()
			step.activity = Mock(CustomActivity)
			if(addStepProperty)
				step.addToStepProperties(new StepProperty(key:stepPropertyKey, value:"invaluable"))
		then:
			step.validate() == expectedOutcome
		where:
			addStepProperty | stepPropertyKey | expectedOutcome
			true            | 'group'         | true
	}

	def "getDescription calls the i18nUtilService"() {
		given:
			def step = new JoinActionStep()
			def i18nUtilService = Mock(I18nUtilService)
			step.i18nUtilService = i18nUtilService
			
			Group.metaClass.static.get = {id -> Mock(Group)}
		when:
			step.description
		then:
			1 * i18nUtilService.getMessage(_)
	}
}

