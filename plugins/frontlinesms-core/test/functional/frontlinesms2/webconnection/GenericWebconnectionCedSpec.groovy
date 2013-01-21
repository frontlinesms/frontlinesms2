package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class GenericWebconnectionCedSpec extends WebconnectionBaseSpec {
	def 'generic option is available on "Select type" screen'() {
		when:
			launchWizard()
		then:
			option('generic').displayed
		and:
			getTitle('generic') == "Other web service"
		and:
			getDescription('generic') == 'Send messages to other web service'
	}

	def "can create and save a HTTP GET external command"() {
		given:
			startAtTab('request')
		when:
			waitFor { requestTab.get.displayed }
			requestTab.url = "http://www.myurl.com"
			requestTab.get.click()
			requestTab.parameters[0].name = "text"
			requestTab.parameters[0].value = "message_body"
		then:
			nextTab(apiTab)
			nextTab(keywordTab)
		when:
			keywordTab.keyword = "SENDME"
			nextTab(confirmTab)
			confirmTab.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "can create and save an HTTP POST external command"() {
		given:
			startAtTab('request')
		when:
			requestTab.url = "http://www.myurl.com"
			requestTab.post.click()
			requestTab.parameters[0].value = "message_body"
			requestTab.parameters[0].name = "text"
		then:
			nextTab(apiTab)
			nextTab(keywordTab)
		when:
			keywordTab.keyword = "SENDME"
		then:
			nextTab(confirmTab)
		when:
			confirmTab.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "can create an external command with no keyword"() {
		given:
			startAtTab('keyword')
		when:
			keywordTab.keyword = ""
			keywordTab.useKeyword('disabled').click() // to disable
			next.click()
		then:
			waitFor { confirmTab.displayed }
		when:
			confirmTab.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "can add and remove any number of parameters to the request"() {
		when:
			launchWizard('generic')
		then:
			waitFor { requestTab.displayed }
		when:
			requestTab.get.click()
			requestTab.url = "http://www.myurl.com"
			requestTab.parameters[0].value = "message_body"
			requestTab.parameters[0].name = "text"
			requestTab.addParam.click()
		then:
			waitFor { requestTab.parameters.size() == 2 }
		when:
			requestTab.parameters[1].value = "contact_name"
			requestTab.parameters[1].name = "contact"
		then:
			nextTab(apiTab)
			nextTab(keywordTab)
		when:
			keywordTab.keyword = "SENDME"
			nextTab(confirmTab)
			previousTab(keywordTab)
			previousTab(apiTab)
			previousTab(requestTab)
			requestTab.parameters[0].remove.click()
		then:
			waitFor { requestTab.parameters.size() == 1 }
			requestTab.parameters[0].value.jquery.val() == "contact_name"
		when:
			nextTab(apiTab)
			nextTab(keywordTab)
			nextTab(confirmTab)
			confirmTab.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "Keyword must be provided if its checkbox is selected"() {
		given:
			startAtTab('confirm')
		when:
			previousTab(keywordTab)
			keywordTab.useKeyword('enabled').jquery.click()//enable keyword
			next.click()
		then:
			waitFor { error.toLowerCase().contains('this field is required') }
	}

	def "Url must be provided"() {
		given:
			startAtTab('request')
		when:
			requestTab.post.click()
			next.click()
		then:
			waitFor { error.contains('Url is required') }
	}

	def "If parameter added a name must be given"() {
		given:
			startAtTab('request')
		when:
			requestTab.url = "http://www.frontlinsms.com.sync"
			requestTab.addParam.click()
		then:
			nextTab(requestTab)
			// N.B. there is no text displayed for this error
	}

	def "Test Webconnection button is displayed on the confirm tab"() {
		given:
			startAtTab('keyword')
		when:
			keywordTab.keyword = ""
			keywordTab.useKeyword('disabled').click() // to disable
			next.click()
		then:
			waitFor { confirmTab.displayed }
			testConnectionButton.displayed
	}

	def 'secret is enabled when API is exposed'() {
		when:
			startAtTab('api')
		then:
			apiTab.secret.disabled
		when:
			apiTab.enableApi = true
		then:
			waitFor { !apiTab.secret.disabled }
	}

	def 'can save a webconnection with API enabled'() {
		given:
			startAtTab 'api'
		when:
			apiTab.enableApi = true
			apiTab.secret = 'spray-on-shoes'
		then:
			nextTab keywordTab
			keywordTab.useKeyword('disabled').jquery.click() // disable keyword
			nextTab confirmTab
		when:
			confirmTab.name = 'random webconnection'
			submit.click()
		then:
			def c = waitFor { Webconnection.findByName('random webconnection') }
			c.apiEnabled
			c.secret == 'spray-on-shoes'
	}

	private def startAtTab(tabName) {
		launchWizard('generic')
		waitFor { requestTab.displayed }
		if(tabName == 'request') return;

		requestTab.url = "http://www.myurl.com"

		nextTab(apiTab)
		if(tabName == 'api') return;

		nextTab(keywordTab)
		if(tabName == 'keyword') return;

		keywordTab.useKeyword('disabled').jquery.click() // disable keyword
		nextTab(confirmTab)

		if(tabName == 'confirm') return;
	}

	private def nextTab(tab) {
		next.click()
		waitFor { tab.displayed }
	}

	private def previousTab(tab) {
		previous.click()
		waitFor { tab.displayed }
	}
}

