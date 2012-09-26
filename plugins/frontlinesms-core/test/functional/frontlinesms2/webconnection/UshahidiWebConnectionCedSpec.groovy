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
}

