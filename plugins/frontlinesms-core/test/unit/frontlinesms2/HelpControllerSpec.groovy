package frontlinesms2

import spock.lang.*

@TestFor(HelpController)
class HelpControllerSpec extends Specification {
	def setup() {
		String.metaClass.markdownToHtml = { "markdown:$delegate" }
	}
	
	def 'If a help file with the given name exists its text will be rendered'() {
		given:
			def helpFileContent = "This is test content for the help"
			File.metaClass.constructor = { String name ->
				if(name != 'web-app/help/testHelp.txt') throw new RuntimeException("unexpected filename: $name")
				return [text:helpFileContent, canRead:{ true }]
			}
			params.helpSection = 'testHelp'
		when:
			controller.section()
		then:
			response.text == 'markdown:' + helpFileContent
	}
	
	def 'If a help file with the given name does not exist error text will be rendered'() {
		given:
			def helpFileContent = "This is test content for the help"
		when:
			controller.section()
		then:
			response.text == 'markdown:This help file is not yet available, sorry.'
	}
}
