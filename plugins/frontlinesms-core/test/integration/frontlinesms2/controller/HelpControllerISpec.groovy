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
			controller.response.text == '<p>help.notfound</p>'
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
		when:
			controller.section()
		then:
			controller.response.contentAsString == '<p>help.notfound</p>'
	}

	def 'If a help file with the given name exists its text will be rendered'() {
		given:
			controller.params.helpSection = 'testHelp'
		when:
			controller.section()
		then:
			controller.response.contentAsString == '<p>This is test content for the help</p>'
	}
}

