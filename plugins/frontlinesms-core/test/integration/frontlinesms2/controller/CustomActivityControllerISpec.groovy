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
			controller.params.jsonToSubmit = '[{"stepId":"","step-type":"join","joinGroup":"5"},{"stepId":"","step-type":"leave","leaveGroup":"3"},{"stepId":"","step-type":"reply","messageText":"This is it"}]'
			controller.params.name = "Custom activity"
		when:
			controller.save()
		then:
			CustomActivity.list()
	}
}