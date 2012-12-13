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

	def 'appSettingsService should be updated properly on new popup display'() {
		when:
			controller.newfeatures()
		then:
			appSettingsService['newfeatures.popup.show.infuture'] == 'false'
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
}
