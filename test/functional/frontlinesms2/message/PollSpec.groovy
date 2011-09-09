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
			launchPollPopup()
			$("a", text:"Confirm").click()
		then:
			waitFor { confirmationTab.displayed }
		when:
			$("input", name:'title').value("POLL NAME")
			done.click()
		then:
			waitFor { !$("div.flash").text().isEmpty() }
			Poll.findByTitle("POLL NAME").responses*.value.containsAll("Yes","No", "Unknown")
	}

	def "should skip recipients tab when do not send message option is chosen"() {
		when:
			launchPollPopup()
		then:
			waitFor { autoSortTab.displayed }
		when:
			next.click()
		then:
			waitFor { autoReplyTab.displayed }
		when:
			next.click()
			println "Tabs with classes: ${tabMenu*.hasClass('ui-state-disabled')}"
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
			responseListTab.displayed 
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
			sleep(500) // FIXME really necessary?
		then:
			autoReplyTab.displayed
		when:
			tabMenu[4].click()
		then:
			waitFor { autoReplyTab.displayed }
	}

	def "should enter instructions for the poll and validate multiple choices user entered"() {
		when:
			launchPollPopup('multiple', 'How often do you drink coffee?')
		then:
			waitFor { responseListTab.displayed }
			!$("label[for='choiceA']").hasClass('bold')
		when:
			$("input", name:'instruction').value("Reply A,B etc")
			keyInData('choiceA', "Never")
			keyInData('choiceB',"Once a day")
			keyInData('choiceC', "Twice a day")
			next.click()
		then:
			waitFor { autoSortTab.displayed }
		when:
			next.click()
		then:
			$("label[for='choiceA']").hasClass('bold')
			waitFor { autoReplyTab.displayed }
			autoReplyText.@disabled
		when:
			$("#send_auto_reply").jquery.trigger('click')
			autoReplyText.value("Thanks for participating...")
			$("#send_auto_reply").jquery.trigger('click')
		then:
			autoReplyText.@disabled
		when:
			$("#send_auto_reply").jquery.trigger('click')
			autoReplyText.value("Thanks for participating...")
			next.click()
		then:
			waitFor { selectRecipientsTab.displayed }
		when:
			next.click()
		then:
			waitFor { confirmationTab.displayed }
			$("#poll-question-text").text() == "How often do you drink coffee? A) Never B) Once a day C) Twice a day"
			$("#confirm-recepients-count").text() == "0 contacts selected (0 messages will be sent)"
			$("#auto-reply-read-only-text").text() == "Thanks for participating..."
		when:	
			$("input", name:'title').value("Cofee Poll")
			done.click()
		then:
			waitFor { $("div.flash").text().contains("The poll has been created!") }
	}

	def keyInData(String selector, String value) {
		def element = $("input", name:selector)
		element.value(value)
		element.jquery.trigger('blur')
	}

	def launchPollPopup(pollType='standard', question=null) {
		to MessagePage
		createActivityButton.click()
		waitFor { createActivityDialog.displayed }
		$("input", name: "activity").value("poll")
		$("#done").click()
		waitFor { at PollCreatePage }
		$("input", name:'poll-type').value(pollType)
		if(question) $("textarea", name:'question').value(question)
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
		
		autoReplyText { autoReplyTab.find("textarea", name:'autoReplyText') }
		
		next { $("#nextPage") }
		prev { $("#prevPage") }
		done { $("#done") }
	}
}

class PollShowPage extends geb.Page {
	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Alice').id}" }
	static at = {
		title.endsWith('Poll')
	}
}
