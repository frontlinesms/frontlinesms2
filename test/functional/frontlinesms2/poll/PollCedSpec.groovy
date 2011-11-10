package frontlinesms2.poll

import frontlinesms2.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.message.PageMessagePending
import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


class PollCedSpec extends PollBaseSpec {
	private def DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyyy hh:mm", Locale.US)
	
	def "should auto populate poll response when a poll with yes or no answer is created"() {
		when:
			launchPollPopup('standard', null)
		then:
			errorMessage.displayed
		when:
			pollForm.question = "question"
			pollForm.'dontSendMessage' = 'no-message'
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
			at PageMessagePending
			Poll.findByTitle("POLL NAME").responses*.value.containsAll("Yes", "No", "Unknown")
	}
	
	def "should require keyword if sorting is enabled"() {
		when:
			launchPollPopup()
		then:
			waitFor { autoSortTab.displayed }
			pollForm.keyword().disabled
		when:
			pollForm.enableKeyword = 'true'
			!pollForm.keyword
		then:
			waitFor { !pollForm.keyword().disabled }
			!pollForm.keyword
		when:
			next.click()
		then:
			waitFor { errorMessage.displayed }
			pollForm.keyword().hasClass('error')
			autoSortTab.displayed
		when:
			pollForm.keyword = 'trigger'
			next.click()
		then:
			waitFor { autoReplyTab.displayed }
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
			waitFor { autoReplyTab.displayed }
			pollForm.autoReplyText().disabled
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
			pollForm.enableKeyword = true
			pollForm.keyword = 'coffee'
			next.click()
		then:
			waitFor { autoReplyTab.displayed }
			pollForm.autoReplyText().disabled
		when:
			pollForm.enableAutoReply = true
		then:
			waitFor { !pollForm.autoReplyText().disabled }
		when:
			
			pollForm.autoReplyText = "Thanks for participating..."
		then:
			waitFor {
				// using jQuery here as seems to be a bug in getting field value the normal way for textarea
				pollForm.autoReplyText().jquery.val() == "Thanks for participating..."
			}
		when:
			pollForm.enableAutoReply = false
		then:	
			waitFor { pollForm.autoReplyText().disabled }
			pollForm.autoReplyText().jquery.val() == "Thanks for participating..."
		when:
			pollForm.enableAutoReply = true
			next.click()
		then:
			waitFor { editMessageTab.displayed }
		when:
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
			pollForm.address = '1234567890'
			addManualAddress.click()
		then:
			waitFor { $('.manual').displayed }
			$("#recipient-count").text() == "1"
		when:
			next.click()
		then:
			waitFor { confirmationTab.displayed }
			$("#poll-message").text() == 'How often do you drink coffee? Reply "COFFEE A" for Never, "COFFEE B" for Once a day, "COFFEE C" for Twice a day.'
			$("#confirm-recepients-count").text() == "1 contacts selected (1 messages will be sent)"
			$("#auto-reply-read-only-text").text() == "Thanks for participating..."
		when:
			pollForm.title = "Coffee Poll"
			done.click()
		then:
			waitFor { Poll.findByTitle("Coffee Poll") }
	}

	def "can enter instructions for the poll and allow user to edit message"() {
		when:
			launchPollPopup('multiple', 'How often do you drink coffee?')
		then:
			waitFor { responseListTab.displayed }
			choiceALabel.hasClass('field-enabled')
			choiceBLabel.hasClass('field-enabled')
		when:
			pollForm.choiceA = 'Never'
			pollForm.choiceB = 'Once a day'
		then:
			waitFor { choiceCLabel.hasClass('field-enabled') }
		when:
			pollForm.choiceC = 'Twice a day'
		then:
			waitFor { choiceDLabel.hasClass('field-enabled') }
		when:
			next.click()
		then:
			waitFor { autoSortTab.displayed }
		when:
			pollForm.enableKeyword = true
			pollForm.keyword = 'coffee'
			next.click()
		then:
			waitFor { autoReplyTab.displayed }
		when:
			next.click()
		then:
			waitFor { editMessageTab.displayed }
			pollForm.messageText().jquery.val() == 'How often do you drink coffee?\nReply "COFFEE A" for Never, "COFFEE B" for Once a day, "COFFEE C" for Twice a day.'
		when:
			$("#messageText").value('How often do you drink coffee? Reply "COFFEE A" for Never, "COFFEE B" for Once a day, "COFFEE C" for Twice a day. Thanks for participating')
			next.click()
		then:
			waitFor { selectRecipientsTab.displayed }
		when:
			pollForm.address = '1234567890'
			addManualAddress.click()
		then:
			waitFor { $('.manual').displayed }
		when:
			next.click()
		then:
			waitFor { confirmationTab.displayed }
			$("#poll-message").text() == 'How often do you drink coffee? Reply "COFFEE A" for Never, "COFFEE B" for Once a day, "COFFEE C" for Twice a day. Thanks for participating'
			$("#confirm-recepients-count").text() == "1 contacts selected (1 messages will be sent)"
		when:
			pollForm.title = "Coffee Poll"
			done.click()
		then:
			waitFor { Poll.findByTitle("Coffee Poll") }
	}
	
// Ajax calls make passing this test incredibly difficult
//	def "should show the right number of messages to be sent to selected recipients"() {
//		when:
//			def longQuestion = 'who' * 54
//			launchPollPopup('standard', longQuestion)
//		then:
//			waitFor { autoSortTab.displayed }
//		when:
//			goToTab(6)
//			pollForm.address = '1234567890'
//			addManualAddress.click()
//		then:
//			waitFor { $('.manual').displayed }
//		when:
//			next.click()
//		then:
//			waitFor { confirmationTab.displayed }
//			$("#confirm-recepients-count").text() == "1 contacts selected (2 messages will be sent)"
//	}
	
	def "should update confirm screen when user decides not to send messages"() {
		when:
			launchPollPopup('standard', "Will you send messages to this poll")
		then:
			waitFor { autoSortTab.displayed }
		when:
			goToTab(6)
			pollForm.address = '1234567890'
			addManualAddress.click()
		then:
			waitFor { $('.manual').displayed }
		when:
			next.click()
		then:
			waitFor { confirmationTab.displayed }
			$("#confirm-recepients-count").text() == "1 contacts selected (1 messages will be sent)"
		when:
			goToTab(1)
			pollForm."dontSendMessage" = true
			goToTab(7)
		then:
			waitFor { $("#no-recepients").displayed }
			$("#no-recepients").text() == "None"
	}
	
	def "can launch export popup"() {
		when:
			Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
			to PageMessageInbox
			$("a", text: "Who is badder? poll").click()
		then:
			waitFor { title == "Poll" }
		when:
			$("#poll-actions").value("export")
		then:	
			waitFor { $("#ui-dialog-title-modalBox").displayed }
	}

	def "can rename a poll"() {
		given:
			Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
		when:
			to PageMessageInbox
			$("a", text: "Who is badder? poll").click()
		then:
			waitFor { title == "Poll" }
		when:
			$("#poll-actions").value("rename")
		then:
			waitFor { $("#ui-dialog-title-modalBox").displayed }
		when:
			$("#title").value("Rename poll")
			$("#done").click()
		then:
			waitFor { $("a", text: 'Rename poll poll') }
			!$("a", text: "Who is badder? poll")
	}

	def "can delete a poll"() {
		when:
			deletePoll()
			waitFor { $("div.flash").displayed }
		then:
			$("title").text() == "Inbox"
			!$("a", text: "Who is badder?")
	}
	
	def "deleted polls show up in the trash section"() {
		setup:
			def poll = deletePoll()
		when:
			go "message/trash/show/${Trash.findByLinkId(poll.id).id}"
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			rowContents[2] == 'Who is badder?'
			rowContents[3] == '0 messages'
			rowContents[4] == DATE_FORMAT.format(Trash.findByLinkId(poll.id).dateCreated)
	}
	
	def "selected poll and its details are displayed"() {
		setup:
			def poll = deletePoll()
		when:
			go "message/trash/show/${Trash.findByLinkId(poll.id).id}"
		then:
			$('#activity-name').text() == poll.title
			$('#activity-date').text() == DATE_FORMAT.format(Trash.findByLinkId(poll.id).dateCreated)
			$('#activity-body').text() == "${poll.getLiveMessageCount()} messages"
	}
	
	def "clicking on empty trash permanently deletes a poll"() {
		setup:
			deletePoll()
		when:
			go "message/trash"
			$("#trash-actions").value("empty-trash")
		then:
			waitFor { $("#ui-dialog-title-modalBox").displayed }
		when:
			$("#title").value("Empty trash")
			$("#done").click()
		then:
			!Poll.findAll()
	}
	
	def deletePoll() {
		def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
		go "message/poll/${poll.id}"
		$("#poll-actions").value("delete")
		waitFor { $("#ui-dialog-title-modalBox").displayed }
		$("#title").value("Delete poll")
		$("#done").click()
		poll
	}
	
	def launchPollPopup(pollType='standard', question='question', enableMessage=true) {
		to PageMessageInbox
		createActivityButton.click()
		waitFor { createActivityDialog.displayed }
		$("input", class: "poll").click()
		$("#submit").click()
		waitFor { at PagePollCreate }
		pollForm.'poll-type' = pollType
		if(question) pollForm.question = question
		pollForm."dontSendMessage" = !enableMessage
		next.click()
	}
	
	def goToTab(tab) {
		$(".tabs-$tab").click()	
	}
}

