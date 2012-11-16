package frontlinesms2.controller

import frontlinesms2.*

class HelpControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller

	def applicationPropertiesService
	
	def setup() {
		controller = new HelpController()
	}

	def cleanup(){
		new File(System.getProperty("user.home") +'/something.properties').delete()
	}
	
	def 'If a help file with the given name exists its text will be rendered'() {
		given:
			controller.params.helpSection = 'test'
		when:
			controller.section()
		then:
			controller.response.text == '<p>This help file is not yet available, sorry.</p>'
	}

	def 'applicationPropertiesService should be updated prooperly on new popup display'(){
		when:
			controller.newfeatures()
		then:
			applicationPropertiesService.showPopupInCurrentSession == false
	}

	@spock.lang.Unroll
	def 'applicationPropertiesService should persist the relevant data when -show when frontlinesms starts checkbox- is changed'(){
		given:
			applicationPropertiesService.propertyFileLocation = System.getProperty("user.home") +'/something.properties'
			controller.params.enableNewFeaturesPopup = inValue
		when:
			controller.updateShowNewFeatures()
		then:
			applicationPropertiesService.showNewFeaturesPopup == outValue
		where:
			inValue|outValue
			true|true
			false|false
	}
}
