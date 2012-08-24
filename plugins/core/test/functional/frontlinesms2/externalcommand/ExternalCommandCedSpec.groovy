package frontlinesms2.externalcommand

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class ExternalCommandCedSpec extends ExternalCommandBaseSpec {

	def "can launch external command screen from create new activity link" () {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			externalcommand.click()
		then:
			waitFor('slow') { at ExternalCommandDialog }
	}

	def "can create and save a HTTP GET external command"() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			externalcommand.click()
		then:
			waitFor('slow') { at ExternalCommandDialog }
		when:
			keywordAndUrl.keyword = "SENDME"
			keywordAndUrl.post.click()
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
			externalcommand.click()
		then:
			waitFor('slow') { at ExternalCommandDialog }
		when:
			keywordAndUrl.keyword = "SENDME"
			keywordAndUrl.get.click()
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

	def "can create an external command with no keyword"() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			externalcommand.click()
		then:
			waitFor('slow') { at ExternalCommandDialog }
		when:
			keywordAndUrl.keyword = ""
			keywordAndUrl.useKeyword.click() // to disable
			keywordAndUrl.post.click()
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

	def "can add and remove any number of parameters to the request"() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			externalcommand.click()
		then:
			waitFor('slow') { at ExternalCommandDialog }
		when:
			keywordAndUrl.keyword = "SENDME"
			keywordAndUrl.get.click()
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
			requestFormat.addParam.click()
		then:
			waitFor { requestFormat.parameters.size() == 2 }
		when:
			requestFormat.parameters(0).value = "contact_name"
			requestFormat.parameters(0).name = "contact"
			next.click()
		then:
			waitFor { confirm.displayed }
		when:
			prev.click()
		then:
			waitFor { requestFormat.displayed }
		when:
			requestFormat.parameters(0).remove.click()
		then:
			waitFor { requestFormat.parameters.size() == 1 }
			requestFormat.parameters(0).value.jquery.val() == "contact_name"
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

}