package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class HelpControllerSpec extends ControllerSpec {
	
	def 'If a help file with the given name exists its text will be rendered'() {
		given:
			registerMetaClass String
			String.metaClass.markdownToHtml = { delegate }
			def helpFile = new File('testHelp.txt')
			def helpFileContent = "This is test content for the help"
			helpFile.text = helpFileContent
			controller.params.helpSection = "testHelp"
		when:
			controller.getSection()
		then:
			renderArgs.text == helpFileContent
		cleanup:
			helpFile.delete()
	}
	
	def 'If a help file with the given name does not exist error text will be rendered'() {
		given:
			registerMetaClass String
			String.metaClass.markdownToHtml = { delegate }
			def helpFileContent = "This is test content for the help"
		when:
			controller.getSection()
		then:
			renderArgs.text != helpFileContent
			renderArgs.text == "This help file is not yet available, sorry."
	}
}
