package frontlinesms2.controller

import spock.lang.*
import frontlinesms2.*
import grails.plugin.spock.*

class CustomActivityControllerISpec extends IntegrationSpec {

	def controller
	def setup() {
		controller = new CustomactivityController()
	}

	def "can save a custom activity"() {
		setup:
			controller.params.jsonToSubmit = '[{"stepId":"","stepType":"join", "stepProperties": [{"key":"group", "value":"5"}, {"key":"group", "value":"10"}] }, {"stepId":"","stepType":"reply", "stepProperties":[{"key":"autoreplyText", "value":"This is it"}] }]'
			controller.params.name = "Custom activity"
		when:
			controller.save()
		then:
			CustomActivity.findByName("Custom activity")
	}

	def "can save a custom activity with a keyword"() {
		setup:
			controller.params.jsonToSubmit = '[{"stepId":"","stepType":"join", "stepProperties": [{"key":"group", "value":"5"}, {"key":"group", "value":"10"}] }, {"stepId":"","stepType":"reply", "stepProperties":[{"key":"autoreplyText", "value":"This is it"}] }]'
			controller.params.name = "Custom activity 2"
			controller.params.sorting = 'enabled'
			controller.params.keywords = 'try,again'
		when:
			controller.save()
		then:
			def customActivity = CustomActivity.findByName("Custom activity 2")
			customActivity
			customActivity.keywords.size() == 2
	}

	def "can save a custom activity with a glabal keyword"() {
		setup:
			controller.params.jsonToSubmit = '[{"stepId":"","stepType":"join", "stepProperties": [{"key":"group", "value":"5"}, {"key":"group", "value":"10"}] }, {"stepId":"","stepType":"reply", "stepProperties":[{"key":"autoreplyText", "value":"This is it"}] }]'
			controller.params.name = "Custom activity 2"
			controller.params.sorting = 'global'
		when:
			controller.save()
		then:
			def customActivity = CustomActivity.findByName("Custom activity 2")
			customActivity
			customActivity.keywords.size() == 1
	}

	def "can edit a custom activity"() {
		setup:
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"1")).save(failOnError:true,flush:true)
			def leaveStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"2")).save(failOnError:true,flush:true)

			def customActivity = new CustomActivity(name:'bummer')
				.addToSteps(joinStep)
				.addToSteps(leaveStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)

			controller.params.ownerId = customActivity.id
			controller.params.jsonToSubmit = '[{"stepId":"","stepType":"join", "stepProperties": [{"key":"group", "value":"5"}, {"key":"group", "value":"10"}] }, {"stepId":"","stepType":"reply", "stepProperties":[{"key":"autoreplyText", "value":"This is it"}] }]'
			controller.params.name = "Do it again"
			controller.params.sorting = 'enabled'
			controller.params.keywords = 'CUSTOM'
		when:
			controller.save()
		then:
			def updatedActivity = CustomActivity.findByName("Do it again")
			updatedActivity
			updatedActivity.steps*.stepProperties*.flatten()*.value.flatten() == ["5", "10", "This is it"]
			updatedActivity.keywords.size() == 1
	}
}