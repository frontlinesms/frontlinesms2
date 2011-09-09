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
			def firstMessageLink = $('#messages tbody tr:nth-child(1) a', href:"/frontlinesms2/message/poll/${poll.id}/show/${message.id}")
		then:
			firstMessageLink.text() == 'Alice'
	}

	def 'selected message and its details are displayed'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
			def poll = Poll.findByTitle('Football Teams')
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
			$("#question").value("question")
			$("input", name:"collect-responses").value('no-message')
			$("a", text:"Confirm").click()
		then:
			waitFor { confirmationTab.displayed }
		when:
			$("input", name:'title').value("POLL NAME")
			done.click()
		then:
			waitFor {$("#confirmation").displayed}
		when:
			$("#confirmation").click()
			println "Responses: ${Poll.findByTitle("POLL NAME").responses}"
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
			tabMenu[1].hasClass("ui-state-disabled")
			tabMenu[4].hasClass("ui-state-disabled")
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
			waitFor { autoReplyTab.displayed }
			autoReplyText.@disabled
		when:
			$("#send_auto_reply").jquery.trigger('click')
			next.click()
		then:
			autoReplyTab.displayed
		when:
			tabMenu[4].click()
		then:
			waitFor { autoReplyTab.displayed }
			errorMessage.displayed
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
			$("label[for='choiceA']").hasClass('bold')
			$("label[for='choiceB']").hasClass('bold')
			!$("label[for='choiceC']").hasClass('bold')
			!$("label[for='choiceD']").hasClass('bold')
			!$("label[for='choiceE']").hasClass('bold')
		when:
			keyInData('choiceA', "Never")
			keyInData('choiceB',"Once a day")
		then:
			true || $("label[for='choiceC']").hasClass('bold')
		when:
			keyInData('choiceC', "Twice a day")
			next.click()
		then:
			waitFor { autoSortTab.displayed }
		when:
			next.click()
		then:
			waitFor { autoReplyTab.displayed }
			autoReplyText.@disabled
		when:
			enableAutoReply.value(true)
			autoReplyText.value("Thanks for participating...")
			enableAutoReply.value(false)
		then:	
			autoReplyText.value() == "Thanks for participating..."
			autoReplyText.@disabled
		when:
			enableAutoReply.value(true)
			next.click()
		then:
			waitFor { selectRecipientsTab.displayed }
		when:
			next.click()
		then:
			waitFor { errorMessage.displayed }
		when:
			$('#address').value('1234567890');
			$('.add-address').click()
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
			$("input", name:'title').value("Coffee Poll")
			done.click()
		then:
			Poll.findByTitle("Coffee Poll")
	}

	def "should launch export popup"() {
		when:
			Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
			go "message"
			$("a", text: "Who is badder?").click()
			waitFor{title == "Poll"}
			$("#poll-actions").value("export")
			$("#poll-actions").jquery.trigger("change")
			waitFor {$("#ui-dialog-title-modalBox").displayed}
		then:
			$("#ui-dialog-title-modalBox").displayed
	}

	def "should be able to rename a poll"() {
		given:
			Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
		when:
			go "message"
			$("a", text: "Who is badder?").click()
			waitFor { title == "Poll" }
			$("#poll-actions").value("renameActivity")
			$("#poll-actions").jquery.trigger("change")
			waitFor { $("#ui-dialog-title-modalBox").displayed }
			$("#title").value("Rename poll")
			$("#done").click()
		then:
			waitFor { $("a", text: 'Rename poll') }
			!$("a", text: "Who is badder?")
	}

	def keyInData(String selector, String value) {
		def element = $("input", name:selector)
		element.value(value)
		element.jquery.trigger('blur')
	}

	def launchPollPopup(pollType='standard', question='question', enableMessage=true) {
		to MessagePage
		createActivityButton.click()
		waitFor { createActivityDialog.displayed }
		$("input", name: "activity").value("poll")
		$("#done").click()
		waitFor { at PollCreatePage }
		$("input", name:'poll-type').value(pollType)
		if(question) $("textarea", name:'question').value(question)
		$("input", name:"collect-responses").value(!enableMessage)
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
		autoSortTab { $("#tabs-3") }
		autoReplyTab { $("#tabs-4") }
		selectRecipientsTab { $("#tabs-5") }
		confirmationTab { $("#tabs-6") }
		
		enableAutoReply { $('input', name:'auto-reply') }
		autoReplyText { autoReplyTab.find("textarea", name:'autoReplyText') }
		
		next { $("#nextPage") }
		prev { $("#prevPage") }
		done { $("#done") }
		
		errorMessage(required:false) { $('.error-panel') }
	}
}

class PollShowPage extends geb.Page {
	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Alice').id}" }
	static at = {
		title.endsWith('Poll')
	}
}
