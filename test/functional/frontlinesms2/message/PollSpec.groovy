package frontlinesms2.message

import frontlinesms2.*
import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class PollSpec extends frontlinesms2.poll.PollGebSpec {
	private def DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyyy hh:mm")
	
	def 'message from alice is first in the list, and links to the show page'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
			def poll = Poll.findByTitle('Football Teams')
		when:
			to PollShowPage
			def firstMessageLink = $('#messages tbody tr:nth-child(1) a', href:"/frontlinesms2/message/poll/$poll.id/show/$message.id")
		then:
			firstMessageLink.text() == 'Alice'
	}

	def 'selected message and its details are displayed'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			to PollShowPage
		then:
			$('#message-details #contact-name').text() == message.src
			$('#message-details #message-date').text() == DATE_FORMAT.format(message.dateCreated)
			$('#message-details #message-body').text() == message.text
	}

	def 'selected message is highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
			def poll = Poll.findByTitle('Football Teams')
			def aliceMessage = Fmessage.findBySrc('Alice')
			def bobMessage = Fmessage.findBySrc('Bob')
		when:
			to PollShowPage
		then:
			$('#messages .selected td:nth-child(3) a').@href == "/frontlinesms2/message/poll/$poll.id/show/$aliceMessage.id"
		when:
			go "message/poll/$poll.id/show/$bobMessage.id"
		then:
			$('#messages .selected td:nth-child(3) a').@href == "/frontlinesms2/message/poll/$poll.id/show/$bobMessage.id"
	}
	
	def 'activities should also list message counts'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollShowPage
		then:
			$('#activities-submenu li')[0..2]*.text() == ['Football Teams', 'Shampoo Brands', 'Rugby Brands']
	}

	def "should auto populate poll response when a poll with yes or no answer is created"() {
		when:
			launchPollPopup('standard', null)
		then:
			errorMessage.displayed
		when:
			pollForm.question = "question"
			pollForm.'collect-responses' = 'no-message'
			$("a", text:"Confirm").click()
		then:
			waitFor { confirmationTab.displayed }
		when:
			pollForm.title = "POLL NAME"
			done.click()
		then:
			waitFor { $("#confirmation").displayed }
		when:
			$("#confirmation").click()
		then:
			Poll.findByTitle("POLL NAME").responses*.value.containsAll("Yes", "No", "Unknown")
	}

	def "should skip recipients tab when do not send message option is chosen"() {
		when:
			launchPollPopup('standard', 'question', false)
		then:
			waitFor { autoSortTab.displayed }
		when:
			next.click()
		then:
			waitFor { autoReplyTab.displayed }
		when:
			next.click()
		then:
			waitFor { confirmationTab.displayed }
			responseListTabLink.hasClass("ui-state-disabled")
			selectRecipientsTabLink.hasClass("ui-state-disabled")
		when:
			prev.click()
		then:
			waitFor { autoReplyTab.displayed }
		when:
			prev.click()
		then:
			waitFor { autoSortTab.displayed }
		when:
			prev.click()
		then:
			waitFor { enterQuestionTab.displayed }
	}


	def "should move to the next tab when multiple choice poll is selected"() {
		when:
			launchPollPopup('multiple')
		then:	
			waitFor { responseListTab.displayed }
	}

	def "should remain in the same tab when auto-reply text is empty"() {
		when:
			launchPollPopup()
		then:
			waitFor { autoSortTab.displayed }
		when:
			next.click()
		then:
			waitFor {
				println "autoreply text: ${$('textarea', name:'autoReplyText')}"
				autoReplyTab.displayed
			}
			pollForm.autoReplyText().@disabled
		when:
			pollForm.enableAutoReply = true
			next.click()
		then:
			autoReplyTab.displayed
		when:
			selectRecipientsTabLink.click()
		then:
			waitFor { errorMessage.displayed }
			autoReplyTab.displayed
	}

	def "should not proceed when less than 2 choices are given for a multi choice poll"() {
		when:
			launchPollPopup('multiple', 'question')
		then:
			waitFor { responseListTab.displayed }
		when:
			next.click()
		then:
			waitFor { errorMessage.displayed }
			responseListTab.displayed
	}

	def "should not proceed when the poll is not named"() {
		when:
			launchPollPopup('standard', 'question', false)
		then:
			waitFor { autoSortTab.displayed }
		when:
			next.click()
		then:
			waitFor { autoReplyTab.displayed }
		when:
			next.click()
		then:
			waitFor { confirmationTab.displayed }
		when:
			done.click()
		then:
			waitFor { errorMessage.displayed }
			confirmationTab.displayed
	}

	def "should enter instructions for the poll and validate multiple choices user entered"() {
		when:
			launchPollPopup('multiple', 'How often do you drink coffee?')
		then:
			waitFor { responseListTab.displayed }
			choiceALabel.hasClass('field-enabled')
			choiceBLabel.hasClass('field-enabled')
			!choiceCLabel.hasClass('field-enabled')
			!choiceDLabel.hasClass('field-enabled')
			!choiceELabel.hasClass('field-enabled')
		when:
			pollForm.choiceA = 'Never'
			pollForm.choiceB = 'Once a day'
		then:
			waitFor { choiceCLabel.hasClass('field-enabled') }
		when:
			pollForm.choiceC = 'Twice a day'
		then:
			waitFor { choiceDLabel.hasClass('field-enabled') }
			next.click()
		then:
			waitFor { autoSortTab.displayed }
		when:
			next.click()
		then:
			waitFor { autoReplyTab.displayed }
			pollForm.autoReplyText().@disabled
		when:
			pollForm.enableAutoReply = true
			pollForm.autoReplyText = "Thanks for participating..."
			pollForm.enableAutoReply = false
		then:	
			pollForm.autoReplyText == "Thanks for participating..."
			pollForm.autoReplyText().@disabled
		when:
			pollForm.enableAutoReply = true
			next.click()
		then:
			waitFor { selectRecipientsTab.displayed }
		when:
			next.click()
		then:
			waitFor { errorMessage.displayed }
		when:
			pollForm.address = '1234567890'
			addManualAddress.click()
		then:
			waitFor { $('.manual').displayed }
		when:
			next.click()
		then:
			waitFor { confirmationTab.displayed }
			$("#poll-question-text").text() == "How often do you drink coffee? A) Never B) Once a day C) Twice a day"
			$("#confirm-recepients-count").text() == "1 contacts selected (1 messages will be sent)"
			$("#auto-reply-read-only-text").text() == "Thanks for participating..."
		when:
			pollForm.title = "Coffee Poll"
			done.click()
		then:
			waitFor { Poll.findByTitle("Coffee Poll") }
	}

	def "should launch export popup"() {
		when:
			Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
			to MessagePage
			$("a", text: "Who is badder?").click()
		then:
			waitFor { title == "Poll" }
		when:
			$("#poll-actions").value("export")
		then:	
			waitFor { $("#ui-dialog-title-modalBox").displayed }
	}

	def "should be able to rename a poll"() {
		given:
			Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
		when:
			to MessagePage
			$("a", text: "Who is badder?").click()
		then:
			waitFor { title == "Poll" }
		when:
			$("#poll-actions").value("renameActivity")
		then:
			waitFor { $("#ui-dialog-title-modalBox").displayed }
		when:
			$("#title").value("Rename poll")
			$("#done").click()
		then:
			waitFor { $("a", text: 'Rename poll') }
			!$("a", text: "Who is badder?")
	}

	def launchPollPopup(pollType='standard', question='question', enableMessage=true) {
		to MessagePage
		createActivityButton.click()
		waitFor { createActivityDialog.displayed }
		$("input", name: "activity").value("poll")
		$("#done").click()
		waitFor { at PollCreatePage }
		pollForm.'poll-type' = pollType
		if(question) pollForm.question = question
		pollForm."collect-responses" = !enableMessage
		next.click()
	}
}

class MessagePage extends geb.Page {
	static url = "message"
	static content = {
		createActivityButton { $("#create-activity a") }
		createActivityDialog(required:false) { $("#ui-dialog-title-modalBox") }
	}
}

class PollCreatePage extends geb.Page {
	static at = { 
		$("#ui-dialog-title-modalBox").text() == "Create Poll"
	}
	static content = {
		tabMenu { $("#tabs li") }
		
		enterQuestionTab { $("#tabs-1") }
		responseListTab { $("#tabs-2") }
		responseListTabLink { tabMenu[1] }
		autoSortTab { $("#tabs-3") }
		autoReplyTab { $("#tabs-4") }
		selectRecipientsTab { $("#tabs-5") }
		selectRecipientsTabLink { tabMenu[4] }
		confirmationTab { $("#tabs-6") }
		
		next { $("#nextPage") }
		prev { $("#prevPage") }
		done { $("#done") }
		
		pollForm { $('form', name:'poll-details') }

		choiceALabel { $('label', for:'choiceA') }
		choiceBLabel { $('label', for:'choiceB') }
		choiceCLabel { $('label', for:'choiceC') }
		choiceDLabel { $('label', for:'choiceD') }
		choiceELabel { $('label', for:'choiceE') }
		
		addManualAddress { $('.add-address') }
		
		errorMessage(required:false) { $('.error-panel') }
	}
}

class PollShowPage extends geb.Page {
	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Alice').id}" }
	static at = {
		title.endsWith('Poll')
	}
}
