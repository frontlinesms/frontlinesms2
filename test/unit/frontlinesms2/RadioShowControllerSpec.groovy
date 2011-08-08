package frontlinesms2

import grails.plugin.spock.ControllerSpec

class RadioShowControllerSpec extends ControllerSpec {

	def "should create a show"() {
		setup:
			mockParams.name = "show name"
			mockDomain(RadioShow)
		when:
			controller.save()
		then:
			RadioShow.findByName("show name")
			controller.redirectArgs.controller == "message"
			controller.redirectArgs.action == "inbox"
	}

	def "save should throw error when validation fails"() {
		setup:
			mockParams.name = ""
			mockDomain(RadioShow)
		when:
			controller.save()
		then:
			controller.flash.message == "Name is not valid"
			controller.redirectArgs.controller == "message"
			controller.redirectArgs.action == "inbox"
	}
}