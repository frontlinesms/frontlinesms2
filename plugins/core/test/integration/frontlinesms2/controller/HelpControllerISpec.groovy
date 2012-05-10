package frontlinesms2.controller

import frontlinesms2.*

class HelpControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	
	def setup() {
		controller = new HelpController()
	}
	
	def 'If a help file with the given name exists its text will be rendered'() {
		given:
			controller.params.helpSection = 'test'
		when:
			controller.section()
		then:
			controller.response.text == '<p>This is test content for the help.</p>'
	}
}
