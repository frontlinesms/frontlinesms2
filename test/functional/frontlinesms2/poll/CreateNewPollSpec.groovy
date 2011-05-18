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
			frmDetails.responses = 'yes'
			btnSave.click()
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
			$('#activities-submenu li')*.text() == ['Football Teams', 'Shampoo Brands']
		cleanup:
			deleteTestPolls()
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
	}
}
