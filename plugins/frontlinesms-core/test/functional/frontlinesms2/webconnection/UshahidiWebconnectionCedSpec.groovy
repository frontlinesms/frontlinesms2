package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*
import spock.lang.*

class UshahidiWebconnectionCedSpec extends WebconnectionBaseSpec {
	def setup(){
		createWebconnections()
	}

	def 'ushahidi option is available on "Select type" screen'() {
		when:
			launchWizard()
		then:
			option('ushahidi').displayed
		and:
			getTitle('ushahidi') == 'Crowdmap / Ushahidi'
		and:
			getDescription('ushahidi') == 'Send messages to CrowdMap or to an Ushahidi server.'
	}

	def 'Configure page for Crowdmap should have info text at the top of page'() {
		when:
			launchWizard('ushahidi')
		then:
			configureUshahidi.subType('crowdmap').click()
			$('.info p')[1].text() == 'The API key for either Crowdmap or Ushahidi can be found in the Settings on the Crowdmap or Ushahidi web site.'
	}

	def 'Api tab disabled when using ushahidi/crowdmap connection type'() {
		when:
			launchWizard('ushahidi')
		then:
			tabByName('webconnection-api').hasClass('disabled-tab')
	}

	def 'when configuring for crowdmap, deploy address has suffix specified'() {
		given:
			launchWizard('ushahidi')
		when:
			configureUshahidi.subType('crowdmap').click()
		then:
			configureUshahidi.urlSuffix.text() == '.crowdmap.com'
			configureUshahidi.crowdmapKeyLabel.text() == 'Crowdmap API Key:'
	}

	def 'when configuring for ushahidi, deploy address is free-form'() {
		given:
			launchWizard('ushahidi') 
		when:
			configureUshahidi.subType('ushahidi').click()
		then:
			configureUshahidi.ushahidiDeployAddress.displayed
			configureUshahidi.ushahidiKeyLabel.text() == 'Ushahidi API Key:'
	}

	@Unroll
	def 'URL and API key must be filled for config page to validate'() {
		given:
			launchWizard('ushahidi')
		when:
			configureUshahidi.subType('ushahidi').click()
		and:
			configureUshahidi.ushahidiDeployAddress = deployAddress
		and:
			configureUshahidi.ushahidiApiKey = apiKey
		and:
			next.click()
		then:
			(valid && keywordTab.keyword.displayed ) || (!valid && errorPanel.displayed)
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
			configureUshahidi.subType('crowdmap').click()
			configureUshahidi.crowdmapDeployAddress.value('my')
			configureUshahidi.crowdmapDeployAddress.jquery.trigger('change')
			configureUshahidi.crowdmapDeployAddress.jquery.trigger('keyup')
			configureUshahidi.crowdmapApiKey.value('a1b2c3d4e5')
			configureUshahidi.crowdmapApiKey.jquery.trigger('change')
			configureUshahidi.crowdmapApiKey.jquery.trigger('keyup')
		when:
			next.click()
			keywordTab.useKeyword('global').click()
			next.click()
		then:
			confirmTab.confirm('service') == 'Crowdmap'
			confirmTab.confirm('url') == 'https://my.crowdmap.com/frontlinesms/'
			confirmTab.crowdmap_api_key == 'a1b2c3d4e5'
			confirmTab.confirm('keyword') == 'None'
	}

	def "editing a web connection should change values"() {
		given:
			to PageMessageWebconnection, UshahidiWebconnection.findByName('Trial')
		when:
			header.moreActions.value("edit").jquery.click()
			waitFor { at WebconnectionWizard }
			configureUshahidi.crowdmapDeployAddress.displayed
			configureUshahidi.crowdmapDeployAddress.value("frontlineCrowd")
			configureUshahidi.crowdmapDeployAddress.jquery.trigger('change')
			configureUshahidi.crowdmapDeployAddress.jquery.trigger('keyup')
			configureUshahidi.crowdmapApiKey.value("2343asdasd")
			next.click()
		and:
			keywordTab.keyword = "Repo"
			next.click()
		then:
			confirmTab.name == "Trial"
			confirmTab.confirm('service') == 'Crowdmap'
			confirmTab.confirm('url') == 'https://frontlineCrowd.crowdmap.com/frontlinesms/'
			confirmTab.crowdmap_api_key == '2343asdasd'
			confirmTab.confirm('keyword') == 'Repo'
		when:
			submit.click()
		then:
			waitFor('very slow'){ summary.message.text() == "The Web Connection has been saved!" }
			submit.click()
			waitFor('very slow') { at PageMessageWebconnection }
			header['name'] == 'trial web connection'
			header['url'] == 'https://frontlinecrowd.crowdmap.com/frontlinesms/'
	}

	private def fillValidConfig() {
		configureUshahidi.subType('crowdmap').click()
		configureUshahidi.crowdmapDeployAddress = 'default'
		configureUshahidi.crowdmapApiKey = 'aaa111bbb222'
		next.click()
		keywordTab.useKeyword('global').click()
	}
}

