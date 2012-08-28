package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class WebConnectionCedSpec extends WebConnectionBaseSpec {

	def "can launch external command screen from create new activity link" () {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			webconnection.click()
		then:
			waitFor('slow') { at WebConnectionWizard }
	}

	def "can create and save a HTTP GET external command"() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			webconnection.click()
		then:
			waitFor('slow') { at WebConnectionWizard }
		when:
			keywordAndUrl.keyword = "SENDME"
			keywordAndUrl.post.click()
			keywordAndUrl.url = "http://www.myurl.com"
			next.click()
		then:
			waitFor { requestFormat.displayed }
		when:
			requestFormat.addParam.click()
		then:
			waitFor { requestFormat.parameters.size() == 1 }
		when:
			requestFormat.parameters(0).value = "message_body"
			requestFormat.parameters(0).name = "text"
			next.click()
		then:
			waitFor { confirm.displayed }
		when:
			confirm.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "can create and save an HTTP POST external command"() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			webconnection.click()
		then:
			waitFor('slow') { at WebConnectionWizard }
		when:
			keywordAndUrl.keyword = "SENDME"
			keywordAndUrl.url = "http://www.myurl.com"
			keywordAndUrl.get.click()
			next.click()
		then:
			waitFor { requestFormat.displayed }
		when:
			requestFormat.addParam.click()
		then:
			waitFor { requestFormat.parameters.size() == 1 }
		when:
			requestFormat.parameters[0].value = "message_body"
			requestFormat.parameters[0].name = "text"
			next.click()
		then:
			waitFor { confirm.displayed }
		when:
			confirm.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "can create an external command with no keyword"() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			webconnection.click()
		then:
			waitFor('slow') { at WebConnectionWizard }
		when:
			keywordAndUrl.keyword = ""
			keywordAndUrl.useKeyword.click() // to disable
			keywordAndUrl.url = "http://www.myurl.com"
			keywordAndUrl.post.click()
			next.click()
		then:
			waitFor { requestFormat.displayed }
		when:
			requestFormat.addParam.click()
		then:
			waitFor { requestFormat.parameters.size() == 1 }
		when:
			requestFormat.parameters[0].value = "message_body"
			requestFormat.parameters[0].name = "text"
			next.click()
		then:
			waitFor { confirm.displayed }
		when:
			confirm.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "can add and remove any number of parameters to the request"() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			webconnection.click()
		then:
			waitFor('slow') { at WebConnectionWizard }
		when:
			keywordAndUrl.keyword = "SENDME"
			keywordAndUrl.get.click()
			keywordAndUrl.url = "http://www.myurl.com"
			next.click()
		then:
			waitFor { requestFormat.displayed }
		when:
			requestFormat.addParam.click()
		then:
			waitFor { requestFormat.parameters.size() == 1 }
		when:
			requestFormat.parameters[0].value = "message_body"
			requestFormat.parameters[0].name = "text"
			requestFormat.addParam.click()
		then:
			waitFor { requestFormat.parameters.size() == 2 }
		when:
			requestFormat.parameters[0].value = "contact_name"
			requestFormat.parameters[0].name = "contact"
			next.click()
		then:
			waitFor { confirm.displayed }
		when:
			prev.click()
		then:
			waitFor { requestFormat.displayed }
		when:
			requestFormat.parameters[0].remove.click()
		then:
			waitFor { requestFormat.parameters.size() == 1 }
			requestFormat.parameters[0].value.jquery.val() == "contact_name"
		when:
			next.click()
		then:
			waitFor { confirm.displayed }
		when:
			confirm.name = "my ext cmd"
			submit.click()
		then:
			waitFor { summary.displayed }
	}

	def "Keyword must be provided if its checkbox is selected"() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			webconnection.click()
		then:
			waitFor('slow') { at WebConnectionWizard }
		when:
			keywordAndUrl.useKeyword.jquery.click()//disable keyword
			keywordAndUrl.url = "www.frontlinesms.com"
			keywordAndUrl.post.click()
			next.click()
		then:
			waitFor { requestFormat.displayed }
		when:
			back.click()
		then:
			waitFor { keywordAndUrl.displayed }
		when:
			keywordAndUrl.useKeyword.jquery.click()//enable keyword
			next.click()
		then:
			waitFor { waitFor {error.text().contains('Keyword is required')} }
	}

	def "Url must be provided"(){
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			webconnection.click()
		then:
			waitFor('slow') { at WebConnectionWizard }
		when:
			keywordAndUrl.keyword = "Sync"
			keywordAndUrl.post.click()
			next.click()
		then:
			waitFor { waitFor {error.text().contains('Url is required')} }
	}

	def "Url must be valid"(){
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			webconnection.click()
		then:
			waitFor('slow') { at WebConnectionWizard }
		when:
			keywordAndUrl.keyword = "Sync"
			keywordAndUrl.url = "frontlinesms"
			keywordAndUrl.post.click()
			next.click()
		then:
			waitFor { waitFor {error.text().contains('Url is must be valid')} }
	}

	def "If parameter added a name must be given"(){
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor{ at CreateActivityDialog }
		when:
			webconnection.click()
		then:
			waitFor('slow') { at WebConnectionWizard }
		when:
			keywordAndUrl.keyword = "Sync"
			keywordAndUrl.url = "www.frontlinsms.com.sync"
			keywordAndUrl.post.click()
			next.click()
		then:
			waitFor { requestFormat.displayed }
		when:
			requestFormat.addParam.click()
		then:
			waitFor { requestFormat.parameters.size() == 1 }
		when:
			next.click()
		then:
			waitFor { waitFor {error.text().contains('Name of paramter must be provided')} }
	}
}