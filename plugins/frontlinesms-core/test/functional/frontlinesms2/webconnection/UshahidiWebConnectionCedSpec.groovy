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

	def 'Configure page for Crowdmap should have info text at the top of page'() {
		when:
			launchWizard()
			option('ushahidi').click()
			next.click()
		then:
			waitFor('veryslow') { configureUshahidi.subType('crowdmap').displayed }
			configureUshahidi.subType('crowdmap').click()
			$('.info').text() == 'The API key for either Crowdmap or Ushahidi can be found in the Settings on the Crowdmap or Ushahidi web site.'
		and:
			$('h2').text() == 'Ushahidi / Crowdmap'
	}

	def 'when configuring for crowdmap, deploy address has suffix specified'() {
		given:
			launchWizard('ushahidi')
		when:
			waitFor('veryslow') { configureUshahidi.subType('crowdmap').displayed }
			configureUshahidi.subType('crowdmap').click()
		then:
			configureUshahidi.urlSuffix.text() == '.crowdmap.com'
			configureUshahidi.crowdmapKeyLabel.text() == 'Crowdmap API Key:'
	}

	def 'when configuring for ushahidi, deploy address is free-form'() {
		given:
			launchWizard('ushahidi')
		when:
			waitFor('veryslow') { configureUshahidi.subType('ushahidi').displayed }
			configureUshahidi.subType('ushahidi').click()
		then:
			configureUshahidi.ushahidiDeployAddress.displayed
			configureUshahidi.ushahidiKeyLabel.text() == 'API Key:'
	}

	@Unroll
	def 'URL and API key must be filled for config page to validate'() {
		given:
			launchWizard('ushahidi')
		when:
			waitFor('veryslow') { configureUshahidi.subType('ushahidi').displayed }
			configureUshahidi.subType('ushahidi').click()
		and:
			configureUshahidi.ushahidiDeployAddress = deployAddress
		and:
			configureUshahidi.ushahidiApiKey = apiKey
		and:
			next.click()
		then:
			(valid && keywordTab.keyword.displayed ) || (!valid && validationError.displayed)
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
		then:
			confirmTab.name.displayed // not sure, but expect this definition will already exist - please update accordingly
	}

	def 'confirm page for Ushahidi Web Connection should display relevant details'() {
		given:
			launchWizard('ushahidi')
		and:
			waitFor('veryslow') { configureUshahidi.subType('crowdmap').displayed }
			configureUshahidi.subType('crowdmap').click()
			configureUshahidi.crowdmapDeployAddress = 'my'
			configureUshahidi.crowdmapApiKey = 'a1b2c3d4e5'
		when:
			next.click()
			keywordTab.useKeyword.click()
			next.click()
		then:
			confirmTab.confirm('service') == 'Crowdmap'
			confirmTab.confirm('url') == 'http://my.crowdmap.com'
			confirmTab.confirm('key') == 'a1b2c3d4e5'
			confirmTab.confirm('keyword') == 'None'
	}

	def "editing a web connection should change values"(){
		given:
			to PageMessageWebConnection, UshahidiWebConnection.findByName('Trial')
		when:
			header.moreActions.value("edit").jquery.click()
			waitFor { at WebConnectionWizard }
			configureUshahidi.subType('crowdmap').click()
			configureUshahidi.crowdmapDeployAddress = "frontlineCrowd"
			configureUshahidi.crowdmapApiKey = "2343asdasd"
			next.click()
		and:
			keywordTab.keyword = "Repo"
			next.click()
		then:
			confirmTab.name == "stanlee"
			confirmTab.confirm('service') == 'Crowdmap'
			confirmTab.confirm('url') == 'http://frontlineCrowd.crowdmap.com'
			confirmTab.confirm('key') == '2343asdasd'
			confirmTab.confirm('keyword') == 'Repo'
		when:
			submit.click()
		then:
			def connection = UshahidiWebConnection.findByName('Trial')
			connection.name == "stanlee"
			connection.url == "http://frontlineCrowd.crowdmap.com"
			connection.requestParameters*.value.containsAll(["2343asdasd"])
	}

	private def fillValidConfig() {
		waitFor('veryslow') { configureUshahidi.subType('crowdmap').displayed }
		configureUshahidi.subType('crowdmap').click()
		configureUshahidi.crowdmapDeployAddress = 'default'
		configureUshahidi.crowdmapApiKey = 'aaa111bbb222'
		next.click()
		keywordTab.useKeyword.click()
	}
}

