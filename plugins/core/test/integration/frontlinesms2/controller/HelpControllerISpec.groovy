package frontlinesms2.controller

import frontlinesms2.*
import org.springframework.web.multipart.commons.CommonsMultipartFile

class HelpControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	
	def setup() {
		controller = new HelpController()
	}
	
	def 'If a help file with the given name exists its text will be rendered'() {
		given:
			def helpFile = new File('testHelp.txt')
			def helpFileContent = "This is test content for the help"
			helpFile.text = helpFileContent
		when:
			def controllerResponse = controller.getSection('testHelp.txt')
		then:
			controllerResponse == helpFileContent
		cleanup:
			helpFile.delete()
	}
}
