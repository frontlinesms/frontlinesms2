package frontlinesms2.poll

import frontlinesms2.*

class CreateNewPollSpec extends PollGebSpec {
	def 'button to create new poll from messages links to new poll page'() {
		when:
			go 'message'
			def btnNewPoll = $('#create-poll a')
		then:
			btnNewPoll.getAttribute('href') == "/frontlinesms2/poll/create"
	}

	def 'button to save new poll with keyword choices and title works'() {
		given:
			createTestPolls()
			def initialPollCount = Poll.count()
		when:
			to CreatePollPage
			frmDetails.title = 'UFOs?'
			frmDetails.responses = 'yes no'
			btnSave.click()
            waitFor { !($("div.flash.message").text().isEmpty()) }
		then:
			Poll.count() == initialPollCount + 1
			title.contains("Inbox")
		cleanup:
			deleteTestPolls()
	}

	def 'existing polls appear in activities section of messages'() {
		given:
			createTestPolls()
		when:
			go 'message'
		then:
			$('#activities-submenu li')*.text() == ['Football Teams', 'Shampoo Brands', 'Rugby Brands']
		cleanup:
			deleteTestPolls()
	}

	def 'each word in response field is its own possible response'() {
		when:
			to CreatePollPage
			frmDetails.title = 'UFOs?'
			frmDetails.responses = 'yes no kidnapped'
			btnSave.click()
            waitFor { !($("div.flash.message").text().isEmpty()) }
			def ufoPoll = Poll.findByTitle("UFOs?")
		then:
			ufoPoll.responses*.value.sort() == ['Unknown', 'kidnapped', 'no', 'yes']
		cleanup:
			deleteTestPolls()
	}
	
	def 'Errors are displayed when poll fails to save'() {
		when:
			to CreatePollPage
			btnSave.click()
		then:
			errorMessages.present
	}
}

class CreatePollPage extends geb.Page {
	static url = 'poll/create'
	static at = {
		// FIXME put in a test here
		true
	}
	static content = {
		frmDetails { $("#poll-details") }
		btnSave { $('input', name:'save') }
		errorMessages(required:false) { $('.flash.errors') }
	}
}
