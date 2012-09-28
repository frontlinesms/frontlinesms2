package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*
import spock.lang.*

class UshahidiWebConnectionCedSpec extends WebConnectionBaseSpec {
	def setup(){
		createWebConnections()
	}

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
			waitFor{ at WebConnectionWizard }
			$('h2').text() == 'Ushahidi / Crowdmap'
		and:
			$('.info').text() == 'The API key for either Crowdmap or Ushahidi can be found in the Settings on the Crowdmap or Ushahidi web site.'
	}

	def 'when configuring for crowdmap, deploy address has suffix specified'() {
		given:
			launchWizard('ushahidi')
		when:
			waitFor{ at WebConnectionWizard }
			configureUshahidi.subType('crowdmap').click()
		then:
			configureUshahidi.crowdmapDeployAddress.displayed
		and:
			configureUshahidi.apiKeyInputLabel.text() == 'Crowdmap API key'
	}

	def 'when configuring for ushahidi, deploy address is free-form'() {
		given:
			launchWizard('ushahidi')
		when:
			waitFor{ at WebConnectionWizard }
			configureUshahidi.subType('ushahidi').click()
		then:
			configureUshahidi.ushahidiDeployAddress.displayed
		and:
			configureUshahidi.apiKeyInputLabel.text() == 'Ushahidi API key'
	}

	@Unroll
	def 'URL and API key must be filled for config page to validate'() {
		given:
			launchWizard('ushahidi')
		when:
			waitFor{ at WebConnectionWizard }
			configureUshahidi.subType('ushahidi')
		and:
			configureUshahidi.ushahidiDeployAddress = deployAddress
		and:
			configureUshahidi.ushahidiApiKey = apiKey
		and:
			next.click()
		then:
			(valid && at (AutomaticSortingTab)) || (!valid && validationError.displayed)
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
			waitFor{ at WebConnectionWizard }
			fillValidConfig()
		when: 'skip past sorting page'
			next.click()
			next.click()
		then:
			confirmTab.name.displayed // not sure, but expect this definition will already exist - please update accordingly
	}

	def 'confirm page for Ushahidi Web Connection should display relevant details'() {
		given:
			launchWizard('ushahidi')
		and:
			waitFor{ at WebConnectionWizard }
			configureUshahidi.subType('crowdmap')
			configureUshahidi.crowdmapDeployAddress = 'my'
			configureUshahidi.crowdmapApiKey = 'a1b2c3d4e5'
		when: 'we skip past the sorting page'
			next.click()
			next.click()
		then:
			confirmTab.confirm('Service') == 'Crowdmap'
			confirmTab.confirm('Address') == 'http://my.crowdmap.com'
			confirmTab.confirm('API key') == 'a1b2c3d4e5'
			confirmTab.confirm('Auto-sort') == 'No messages will be autosorted but you should check what the correct text is here when implementing thanks ;-)'
	}

	def "editing a web connection should change values"(){
		given:
			to PageMessageWebConnection, UshahidiWebConnection.findByName('Trial')
		when:
			header.moreActions.value("edit").jquery.click()
			waitFor { at WebConnectionWizard }
			configureUshahidi.subType('crowdmap').click()
			configureUshahidi.crowdmapDeployAddress = "frontlineCrowd"
			configureUshahidi.apiKeyInputLabel = "2343asdasd"
			next.click()
		and:
			keywordTab.keywordTab = "Repo"
			next.click()
		then:
			confirmTab.name == "stanlee"
			confirmTab.confirm('Service') == 'Crowdmap'
			confirmTab.confirm('Address') == 'http://frontlineCrowd.crowdmap.com'
			confirmTab.confirm('API key') == '2343asdasd'
			confirmTab.confirm('Auto-sort') == 'No messages will be autosorted but you should check what the correct text is here when implementing thanks ;-)'
		when:
			submit.click()
		then:
			def connection = UshahidiWebConnection.findByName('Trial')
			connection.name == "stanlee"
			connection.url == "http://frontlineCrowd.crowdmap.com"
			connection.requestParameters*.value.containsAll(["2343asdasd"])
	}

	private def fillValidConfig() {
		configureUshahidi.subType('crowdmap')
		configureUshahidi.crowdmapDeployAddress = 'default'
		configureUshahidi.crowdmapApiKey = 'aaa111bbb222'
	}
}

