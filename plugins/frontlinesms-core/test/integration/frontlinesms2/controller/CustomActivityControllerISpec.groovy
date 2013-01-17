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
			CustomActivity.list()
	}
}