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
			frmDetails.choiceA = 'yes'
			frmDetails.choiceB = 'no'
			$("a", text:"confirm").click()
			frmDetails.title = 'UFOs?'
			btnSave.click()
	
            waitFor { !($("div.flash.message").text().isEmpty()) }
		then:
			Poll.count() == initialPollCount + 1
			title.contains("Inbox")
		cleanup:
			deleteTestPolls()
	}

	def "should not allow to proceed to the next tab if blank auto response is given" () {
		when:
			go 'message'
		
			$("a", text:"Poll").click()
			waitFor { $("div", id:"tabs-1").displayed}
			$("a", text:'Automatic reply').click()
			waitFor { $("div", id:"tabs-3").displayed}
            $('textArea', name: "autoReplyText").jquery.trigger('focus')
        then:
			$('#send_auto_reply_check').getAttribute("checked")
		when:
			frmDetails.autoReplyText = ''
			$("#tabs-3 a", text:"Next").click()
			waitFor { !$('.error-panel').text().isEmpty()}
        then:
			!$('.error-panel').text().isEmpty()
	}

	def 'existing polls appear in activities section of messages'() {
		given:
			createTestPolls()
		when:
			go 'message'
		then:
			$('#activities-submenu li')[0].text().contains('Football Teams') // TODO: find/implement array contains
			$('#activities-submenu li')[1].text().contains('Shampoo Brands')
			$('#activities-submenu li')[2].text().contains('Rugby Brands')
		cleanup:
			deleteTestPolls()
	}

	def 'each word in response field is its own possible response'() {
		when:
			to CreatePollPage
			frmDetails.title = 'UFOs?'
			frmDetails.choiceA = 'yes' 
			frmDetails.choiceB = 'no' 
			frmDetails.choiceC = 'kidnapped' 
			btnSave.click()
            waitFor { !($("div.flash.message").text().isEmpty()) }
			def ufoPoll = Poll.findByTitle("UFOs?")
		then:
			ufoPoll.responses*.value.sort() == ['Unknown', 'kidnapped', 'no', 'yes']
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
		errorMessages(required:false) { $('.flash.errors') }
	}
}
