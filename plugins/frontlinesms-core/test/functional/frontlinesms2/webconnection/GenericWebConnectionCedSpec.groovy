package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class GenericWebConnectionCedSpec extends WebConnectionBaseSpec {
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
		when:
			launchWizard('generic')
		then:
			waitFor { requestTab.displayed }
		when:
			requestTab.post.click()
			requestTab.url = "http://www.myurl.com"
			requestTab.parameters[0].name = "text"
			requestTab.parameters[0].value = "message_body"
			next.click()
		then:
			waitFor { keywordTab.displayed }
		when:
			keywordTab.keyword = "SENDME"
			next.click()
		then:
			waitFor { confirmTab.displayed }
		when:
			confirmTab.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "can create and save an HTTP POST external command"() {
		given:
			launchWizard('generic')
		when:
			keywordTab.keyword = "SENDME"
			next.click()
		then:
			waitFor { requestTab.displayed }
		when:
			requestTab.url = "http://www.myurl.com"
			requestTab.get.click()
			requestTab.parameters[0].value = "message_body"
			requestTab.parameters[0].name = "text"
			next.click()
		then:
			waitFor { confirmTab.displayed }
		when:
			confirmTab.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "can create an external command with no keyword"() {
		given:
			launchWizard('generic')
		when:
			keywordTab.keyword = ""
			keywordTab.useKeyword.click() // to disable
			next.click()
		then:
			waitFor { requestTab.displayed }
		when:
			requestTab.url = "http://www.myurl.com"
			requestTab.post.click()
			requestTab.parameters[0].value = "message_body"
			requestTab.parameters[0].name = "text"
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
		given:
			launchWizard('generic')
		when:
			keywordTab.keyword = "SENDME"
			next.click()
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
			next.click()
		then:
			waitFor { confirmTab.displayed }
		when:
			previous.click()
		then:
			waitFor { requestTab.displayed }
		when:
			requestTab.parameters[0].remove.click()
		then:
			waitFor { requestTab.parameters.size() == 1 }
			requestTab.parameters[0].value.jquery.val() == "contact_name"
		when:
			next.click()
		then:
			waitFor { confirmTab.displayed }
		when:
			confirmTab.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "Keyword must be provided if its checkbox is selected"() {
		given:
			launchWizard('generic')
		when:
			keywordTab.useKeyword.jquery.click()//disable keyword
			next.click()
		then:
			waitFor { requestTab.displayed }
		when:
			previous.click()
		then:
			waitFor { keywordTab.displayed }
		when:
			keywordTab.useKeyword.jquery.click()//enable keyword
			next.click()
		then:
			waitFor { error.contains('Keyword is required') }
	}

	def "Url must be provided"() {
		given:
			launchWizard('generic')
		when:
			keywordTab.keyword = "Sync"
			next.click()
		then:
			waitFor { requestTab.displayed }
		when:
			requestTab.post.click()
			next.click()
		then:
			waitFor { error.contains('Url is required') }
	}

	def "If parameter added a name must be given"() {
		given:
			launchWizard('generic')
		when:
			keywordTab.keyword = "Sync"
			next.click()
		then:
			waitFor { requestTab.displayed }
		when:
			requestTab.url = "http://www.frontlinsms.com.sync"
			requestTab.addParam.click()
			next.click()
		then:
			requestTab.displayed
	}
}

