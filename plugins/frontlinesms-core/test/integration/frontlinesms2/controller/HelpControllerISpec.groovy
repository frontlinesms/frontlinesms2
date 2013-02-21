package frontlinesms2.controller

import frontlinesms2.*

class HelpControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller

	def appSettingsService
	
	def setup() {
		controller = new HelpController()
	}
	
	def 'If a help file with the given name exists its text will be rendered'() {
		given:
			controller.params.helpSection = 'test'
		when:
			controller.section()
		then:
			controller.response.text == '<p>This help file is not yet available, sorry.</p>'
	}

	def 'appSettingsService should be updated to hide for rest of session on new popup display'() {
		when:
			controller.newfeatures()
		then:
			appSettingsService['newfeatures.popup.show.immediately'] == 'false'
	}

	@spock.lang.Unroll
	def 'appSettingsService should persist the relevant data when -show when frontlinesms starts checkbox- is changed'() {
		given:
			controller.params.enableNewFeaturesPopup = inValue
		when:
			controller.updateShowNewFeatures()
		then:
			appSettingsService['newfeatures.popup.show.infuture'] == outValue
		where:
			inValue | outValue
			true    | 'true'
			false   | 'false'
	}

	def 'If a help file with the given name does not exist error text will be rendered'() {
		given:
			String.metaClass.markdownToHtml = { "markdown:$delegate" }
			def helpFileContent = "This is test content for the help"
		when:
			controller.section()
		then:
			controller.response.contentAsString == 'markdown:This help file is not yet available, sorry.'
	}

	def 'If a help file with the given name exists its text will be rendered'() {
		given:
			def helpFileContent = "This is test content for the help"
			registerMetaClass File
			File.metaClass.constructor = { String name ->
				if(name != 'web-app/help/testHelp.txt') throw new RuntimeException("unexpected filename: $name")
				return [text:helpFileContent, canRead:{ true }]
			}
			controller.params.helpSection = 'testHelp'
		when:
			controller.section()
		then:
			controller.response.contentAsString == 'markdown:' + helpFileContent
	}
}

