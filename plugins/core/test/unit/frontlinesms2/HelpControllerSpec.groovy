package frontlinesms2

import spock.lang.*

@TestFor(HelpController)
class HelpControllerSpec extends Specification {
	def setup() {
		String.metaClass.markdownToHtml = { "markdown:$delegate" }
	}
	
	def 'If a help file with the given name exists its text will be rendered'() {
		given:
			def helpFile = new File('testHelp.txt')
			def helpFileContent = "This is test content for the help"
			helpFile.text = helpFileContent
			params.helpSection = 'testHelp'
		when:
			controller.section()
		then:
			response.text == 'markdown:' + helpFileContent
		cleanup:
			helpFile.delete()
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
