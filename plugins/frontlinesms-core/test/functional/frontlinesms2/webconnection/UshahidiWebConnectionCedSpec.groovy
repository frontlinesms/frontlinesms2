package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class UshahidiWebConnectionCedSpec extends WebConnectionBaseSpec {
	def 'ushahidi option is available on "Select type" screen'() {
		when:
			launchWizard()
		then:
			option('ushahidi').displayed
		and:
			getTitle('ushahidi') == 'Crowdmap / Ushahidi'
		and:
			getDescription('ushahidi') == 'Send messages to CrowdMap or to an Ushahidi server'
	}

	def 'configure page should have title and description'() {
		when:
			launchWizard('ushahidi')
		then:
			$('h2').text() == 'Ushahidi / Crowdmap'
		and:
			$('.info').text() == 'The API key for either Crowdmap or Ushahidi can be found in the Settings on the Crowdmap or Ushahidi web site.'
	}

	def 'when configuring for crowdmap, deploy address has suffix specified'() {
		given:
			launchWizard('ushahidi')
		when:
			subType('crowdmap').click()
		then:
			crowdmapDeployAddress.displayed
		and:
			apiKeyInputLabel.text() == 'Crowdmap API key'
	}

	def 'when configuring for ushahidi, deploy address is free-form'() {
		given:
			launchWizard('ushahidi')
		when:
			subType('ushahidi').click()
		then:
			ushahidiDeployAddress.displayed
		and:
			apiKeyInputLabel.text() == 'Ushahidi API key'
	}

	@Unroll
	def 'URL and API key must be filled for config page to validate'() {
		given:
			launchWizard('ushahidi')
		when:
			subType('ushahidi')
		and:
			ushahidiDeployAddress = deployAddress
		and:
			ushahidiApiKey = apiKey
		and:
			next.click()
		then:
			(valid && at AutomaticSortingTab) || (!valid && validationError.displayed)
		where:
			deployAddress     | apiKey       | valid
			'www.example.com' | 'ABCDE12345' | true
			''                | 'ABCDE12345' | false
			'www.example.com' | ''           | false
			''                | ''           | false
	}

	def 'name field is displayed on confirm screen'() {
		given:
			launchWizard('ushahidi')
		and:
			fillValidConfig()
		when: 'skip past sorting page'
			next.click()
			next.click()
		then:
			connectionNameField.displayed // not sure, but expect this definition will already exist - please update accordingly
	}

	def 'confirm page for Ushahidi Web Connection should display relevant details'() {
		given:
			launchWizard('ushahidi')
		and:
			subType('crowdmap')
			crowdmapDeployAddress = 'my'
			crowdmapApiKey = 'a1b2c3d4e5'
		when: 'we skip past the sorting page'
			next.click()
			next.click()
		then:
			at ConfirmTab
		and:
			confirm('Service') == 'Crowdmap'
			confirm('Address') == 'http://my.crowdmap.com'
			confirm('API key') == 'a1b2c3d4e5'
			confirm('Auto-sort') == 'No messages will be autosorted but you should check what the correct text is here when implementing thanks ;-)'
	}

	private def fillValidConfig() {
		subType('crowdmap')
		crowdmapDeployAddress = 'default'
		crowdmapApiKey = 'aaa111bbb222'
	}
}

