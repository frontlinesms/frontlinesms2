package frontlinesms2

import grails.plugin.spock.ControllerSpec

class RadioShowControllerIntegrationSpec extends ControllerSpec {

	def controller

	def setup() {
		controller = new RadioShowController()
	}

	def "should create a show"() {
		setup:
			controller.params.name = "show name"
		when:
			controller.save()
			def showInstance = RadioShow.findByName("show name")
		then:
			showInstance
			controller.redirectArgs.showId
			controller.redirectArgs.controller == "message"
			controller.redirectArgs.action == "show"
			controller.redirectArgs.showId == showInstance.id
	}


}