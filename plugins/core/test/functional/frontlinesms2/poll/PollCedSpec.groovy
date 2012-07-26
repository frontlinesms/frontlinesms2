package frontlinesms2.poll

import frontlinesms2.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.message.PageMessagePending
import frontlinesms2.popup.*
import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


class PollCedSpec extends PollBaseSpec {
	private def DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US)
	
	def "entering correct url will load poll"() {
		// also a test of PageMessageActivity.convertToPath
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
		then:
			header.title == 'football teams poll'
		when:
			to PageMessagePoll, Poll.findByName('Football Teams'), Fmessage.findBySrc('Alice')
		then:
			header.title == 'football teams poll'
		when:
			to PageMessagePoll, Poll.findByName('Football Teams').id, 2
		then:
			header.title == 'football teams poll'

	}

	def "should auto populate poll response when a poll with yes or no answer is created"() {
		when:
			launchPollPopup('yesNo', null)
		then:
			errorPanel.displayed
		when:
			tab(1).click()
			compose.question = "question"
			compose.dontSendQuestion.click()
			setAliases();
			tab(8).click()
		then:
			confirm.pollName.displayed
		when:
			confirm.pollName = "POLL NAME"
			submit.click()
		then:
			waitFor { summary.displayed }
			Poll.findByName("POLL NAME").responses*.value.containsAll("Yes", "No", "Unknown")
	}
	
	def "should require keyword if sorting is enabled"() {
		when:
			launchPollPopup()
			setAliases()
			next.click()
		then:
			waitFor { sort.displayed }
			sort.keyword.disabled
		when:
			sort.sort.click()
		then:
			waitFor { !sort.keyword.disabled }
		when:
			next.click()
		then:
			waitFor { errorPanel.displayed }
			sort.keyword.hasClass('error')
			sort.displayed
		when:
			sort.keyword = 'trigger'
			next.click()
		then:
			waitFor { autoreply.displayed }
	}

	def "Aliases tab should disabled when poll popup first loads"() {
		when:
			launchPollPopup()
		then:
			aliases.hasClass('ui-tabs-hide')
	}

	def "Aliases tab should be enabled if sorting is enabled"() {
		when:
			launchPollPopup('yesNo','question',true)
			next.click()
		then:
			at SortTab
			sort.keyword.disabled
		when:
			sort.sort.click()
		then:
			aliases.hasClass('ui-tabs-hide')
	}

	def "should skip recipients tab when do not send message option is chosen"() {
		when:
			launchPollPopup('yesNo', 'question', false)
			setAliases()
			next.click()
		then:
			waitFor { sort.displayed }
		when:
			next.click()
		then:
			waitFor { autoreply.displayed }
		when:
			next.click()
		then:
			waitFor { confirm.displayed }
			tab(2).hasClass("disabled-tab")
			tab(7).hasClass("disabled-tab")
		when:
			previous.click()
		then:
			waitFor { autoreply.displayed }
		when:
			previous.click()
		then:
			waitFor { sort.displayed }
		when:
			previous.click()
		then:
			waitFor { aliases.displayed }
		when:
			previous.click()
		then:
			waitFor { compose.displayed }
	}


	def "should move to the next tab when multiple choice poll is selected"() {
		when:
			launchPollPopup('multiple')
		then:	
			waitFor { response.displayed }
	}

	def "should not proceed when less than 2 choices are given for a multi choice poll"() {
		when:
			launchPollPopup('multiple', 'question')
		then:
			waitFor { response.displayed }
		when:
			next.click()
		then:
			waitFor { errorPanel.displayed }
			response.displayed
	}

	def "should not proceed when the poll is not named"() {
		when:
			launchPollPopup('yesNo', 'question', false)
			setAliases()
			next.click()
		then:
			waitFor { sort.displayed }
		when:
			next.click()
		then:
			waitFor { autoreply.displayed }
		when:
			next.click()
		then:
			waitFor { confirm.displayed }
		when:
			submit.click()
		then:
			waitFor { errorPanel.displayed }
			confirm.displayed
	}
	
	def "should enter instructions for the poll and validate multiple choices user entered"() {
		when:
			launchPollPopup('multiple', 'How often do you drink coffee?')
		then:
			waitFor { response.displayed }
			response.label("A").hasClass('field-enabled')
			response.label("B").hasClass('field-enabled')
			!response.label("C").hasClass('field-enabled')
			!response.label("D").hasClass('field-enabled')
			!response.label("E").hasClass('field-enabled')
		when:
			response.choice("A").jquery.val('Never')
			response.choice("B").jquery.val('Once a day')
			// need to trigger keyup to enable next field
			response.choice("B").jquery.trigger('keyup')

		then:
			waitFor { response.label("C").hasClass('field-enabled') }
		when:
			response.choice("C").jquery.val('Twice a day')
			// need to trigger keyup to enable next field
			response.choice("C").jquery.trigger('keyup')
		then:
			waitFor { response.label("D").hasClass('field-enabled') }
			next.click()
		then:
			setAliases()
			next.click()
			waitFor { sort.displayed }
		when:
			sort.sort.click()
			sort.keyword = 'coffee'
			next.click()
		then:
			waitFor { autoreply.displayed }
			autoreply.text.disabled
		when:
			autoreply.autoreplyCheck = true
		then:
			waitFor { !autoreply.text.disabled }
		when:
			
			autoreply.text = "Thanks for participating..."
		then:
			waitFor {
				// using jQuery here as seems to be a bug in getting field value the normal way for textarea
				autoreply.text.jquery.val() == "Thanks for participating..."
			}
		when:
			autoreply.autoreplyCheck = false
		then:	
			waitFor { autoreply.text.disabled }
			autoreply.text.jquery.val() == "Thanks for participating..."
		when:
			autoreply.autoreplyCheck = true
			next.click()
		then:
			waitFor { edit.displayed }
		when:
			next.click()
		then:
			waitFor { recipients.displayed }
		when:
			next.click()
		then:
			waitFor { errorPanel.displayed }
		when:
			recipients.addField = '1234567890'
			recipients.addButton.click()
			recipients.addField = '1234567890'
			recipients.addButton.click()
		then:
			waitFor { recipients.manual.size() == 1 }
			recipients.count == 1
		when:
			next.click()
		then:
			waitFor { confirm.displayed }
			confirm.message == 'How often do you drink coffee? Reply "COFFEE A" for Never, "COFFEE B" for Once a day, "COFFEE C" for Twice a day.'
			confirm.recipientCount == "1 contacts selected"
			confirm.messageCount == "1 messages will be sent"
			confirm.autoreply == "Thanks for participating..."
		when:
			confirm.pollName = "Coffee Poll"
			submit.click()
		then:
			waitFor { Poll.findByName("Coffee Poll") }
	}

	def "can enter instructions for the poll and allow user to edit message"() {
		when:
			launchPollPopup('multiple', 'How often do you drink coffee?')
		then:
			waitFor { response.displayed }
			response.label("A").hasClass('field-enabled')
			response.label("B").hasClass('field-enabled')
		when:
			response.choice("A").jquery.val('Never')
			response.choice("B").jquery.val('Once a day')
			response.choice("B").jquery.trigger("keyup")
		then:
			waitFor { response.label("C").hasClass('field-enabled') }
		when:
			response.choice("C").jquery.val('Twice a day')
			response.choice("C").jquery.trigger("keyup")
		then:
			waitFor { response.label("D").hasClass('field-enabled') }
		when:
			setAliases()
			next.click()
		then:
			waitFor { sort.displayed }
		when:
			sort.sort.click()
			sort.keyword = 'coffee'
			next.click()
		then:
			waitFor { autoreply.displayed }
		when:
			next.click()
		then:
			waitFor { edit.displayed }
			edit.text.jquery.val() == 'How often do you drink coffee?\nReply "COFFEE A" for Never, "COFFEE B" for Once a day, "COFFEE C" for Twice a day.'
		when:
			edit.text.value('How often do you drink coffee? Reply "COFFEE A" for Never, "COFFEE B" for Once a day, "COFFEE C" for Twice a day. Thanks for participating')
			next.click()
		then:
			waitFor { recipients.displayed }
		when:
			recipients.addField = '1234567890'
			recipients.addButton.click()
		then:
			waitFor { recipients.manual.size() == 1 }
			recipients.count == 1
		when:
			next.click()
		then:
			waitFor { confirm.displayed }
			confirm.message == 'How often do you drink coffee? Reply "COFFEE A" for Never, "COFFEE B" for Once a day, "COFFEE C" for Twice a day. Thanks for participating'
			confirm.recipientCount == "1 contacts selected"
			confirm.messageCount == "1 messages will be sent"
		when:
			confirm.pollName = "Coffee Poll"
			submit.click()
		then:
			waitFor {Poll.findByName("Coffee Poll") }
	}
	
	def "should update confirm screen when user decides not to send messages"() {
		when:
			launchPollPopup('yesNo', "Will you send messages to this poll")
		then:
			setAliases()
			next.click()
			waitFor { sort.displayed }
		when:
			tab(7).click()
			recipients.addField = '1234567890'
			recipients.addButton.click()
		then:
			waitFor { recipients.manual.size() == 1 }
			recipients.count == 1
		when:
			next.click()
		then:
			waitFor { confirm.displayed }
			confirm.recipientCount == "1 contacts selected"
			confirm.messageCount == "1 messages will be sent"
		when:
			tab(1).click()
			compose.dontSendQuestion = true
			tab(8).click()
		then:
			waitFor { confirm.noRecipients.displayed }
	}
	
	def "can launch export popup"() {
		when:
			def poll = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
			poll.addToResponses(key: 'A', value: 'Michael-Jackson')
			poll.addToResponses(key: 'B', value: 'Chuck-Norris')
			poll.addToResponses(key: 'Unknown', value: 'Unknown')
			poll.save(failOnError:true, flush:true)
			to PageMessageInbox
		then:
			at PageMessageInbox
		when:	
			bodyMenu.activityLink("Who is badder?").click()
		then:
			waitFor { header.title == "who is badder? poll" }
			at PageMessagePoll
		when:
			moreActions.value("export").click()
		then:	
			waitFor { at ExportDialog }
	}

	def "can rename a poll"() {
		when:
			def poll = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
			poll.addToResponses(key: 'A', value: 'Michael-Jackson')
			poll.addToResponses(key: 'B', value: 'Chuck-Norris')
			poll.addToResponses(key: 'Unknown', value: 'Unknown')
			poll.save(failOnError:true, flush:true)
			to PageMessageInbox
		then:
			at PageMessageInbox
		when:	
			bodyMenu.activityLink("Who is badder?").click()
		then:
			waitFor { header.title == "who is badder? poll" }
			at PageMessagePoll
		when:
			moreActions.value("rename").click()
		then:
			waitFor { at RenameDialog }
		when:
			name = "rename poll"
			done.click()
		then:
			waitFor { at PageMessageInbox }
			header.title == "rename poll poll"
	}

	def "can delete a poll"() {
		when:
			deletePoll()
			waitFor { notifications.flashMessage.displayed }
		then:
			at PageMessageInbox
			bodyMenu.activityLinks.size() == 1
	}
	
	def "deleted polls show up in the trash section"() {
		// FIXME: rewrite when trash page definitions exist
		setup:
			def poll = deletePoll()
		when:
			go "message/trash/show/${Trash.findByObjectId(poll.id).id}"
			def rowContents = $('#message-list .main-table tr:nth-child(2) td')*.text()
		then:
			rowContents[2] == 'Who is badder?'
			rowContents[3] == '0 message(s)'
			rowContents[4] == DATE_FORMAT.format(Trash.findByObjectId(poll.id).dateCreated)
	}
	
	def "selected poll and its details are displayed"() {
		// FIXME: rewrite when trash page definitions exist
		setup:
			def poll = deletePoll()
		when:
			go "message/trash/show/${Trash.findByObjectId(poll.id).id}"
		then:
			$('#message-detail-sender').text() == "${poll.name} poll"
			$('#message-detail-date').text() == DATE_FORMAT.format(Trash.findByObjectId(poll.id).dateCreated)
			$('#message-detail-content').text() == "${poll.getLiveMessageCount()} messages"
	}
	
	def "clicking on empty trash permanently deletes a poll"() {
		// FIXME: rewrite when trash page definitions exist
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
	
	def "user can edit an existing poll"() {
		setup:
			def poll = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
			poll.addToResponses(key: 'choiceA', value: 'Michael-Jackson')
			poll.addToResponses(key: 'choiceB', value: 'Chuck-Norris')
			poll.addToResponses(key: 'Unknown', value: 'Unknown')
			poll.save(failOnError:true, flush:true)
		when:
			to PageMessageInbox
			bodyMenu.activityLinks[1].click()
		then:
			waitFor { at PageMessagePoll }
		when:
			moreActions.value("edit").click()
		then:
			waitFor('slow') { at PollDialog }
			compose.question == 'question'
			compose.pollType == "multiple"
		when:
			compose.question = "Who is worse?"
			compose.dontSendQuestion = false
			next.click()
		then:
			waitFor { response.displayed }
			response.choice("A").jquery.val("Michael-Jackson")
			response.choice("B").jquery.val("Chuck-Norris")
		when:
			response.choice("C").jquery.val("Bruce Vandam")
			next.click()
			setAliases()
			next.click()
		then:
			waitFor { sort.displayed }
		when:
			goToTab(7)
			recipients.addField = '1234567890'
			recipients.addButton.click()
		then:
			waitFor { recipients.manual.size() == 1 }
		when:
			next.click()
			confirm.pollName = 'Who is badder?'
			done.click()
		then:
			waitFor { Poll.findByName("Who is badder?").responses*.value.containsAll("Michael-Jackson", "Chuck-Norris", "Bruce Vandam") }		
	}
	
	def "should display errors when poll validation fails"() {
		given:
			def poll = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
			poll.addToResponses(key: 'A', value: 'Michael-Jackson')
			poll.addToResponses(key: 'B', value: 'Chuck-Norris')
			poll.addToResponses(key: 'Unknown', value: 'Unknown')
			poll.save(failOnError:true, flush:true)
			assert Poll.count() == 1
		when:
			launchPollPopup('yesNo', 'question', false)
		then:
			setAliases()
			next.click()
			waitFor { sort.displayed }
		when:
			goToTab(8)
			confirm.pollName = 'Who is badder?'
			done.click()
		then:
			assert Poll.count() == 1
			at PollDialog
			waitFor { errorPanel.displayed }
	}

	def "Choices for a saved poll should validate as required"() {
		setup:
			def poll = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
			poll.addToResponses(key: 'A', value: 'Michael-Jackson')
			poll.addToResponses(key: 'B', value: 'Chuck-Norris')
			poll.addToResponses(key: 'Unknown', value: 'Unknown')
			poll.save(failOnError:true, flush:true)
			poll.refresh()
		when:
			to PageMessagePoll, poll
		then:
			moreActions.value("edit").click()
		when:
			waitFor('slow') { at PollDialog }
			next.click()
		then:
			response.choice("B").jquery.val("")
			response.choice("B").jquery.trigger('keyup')
		when:
			next.click()
		then:
			response.choice("B").hasClass("error")
			response.errorLabel("B").text().contains("A saved choice cannot")
	}
	// TODO: add alias-specific tests

	def deletePoll() {
		def poll = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
		poll.addToResponses(key: 'A', value: 'Michael-Jackson')
		poll.addToResponses(key: 'B', value: 'Chuck-Norris')
		poll.addToResponses(key: 'Unknown', value: 'Unknown')
		poll.save(failOnError:true, flush:true)
		to PageMessagePoll, poll
		moreActions.value("delete")
		waitFor { at DeleteDialog }
		done.click()
		poll
	}

	def launchPollPopup(pollType='yesNo', question='question', enableMessage=true) {
		to PageMessageInbox
		bodyMenu.newActivity.click()
		waitFor('slow') { at CreateActivityDialog }
		poll.click()
		waitFor('slow') { at PollDialog }
		
		pollType=='yesNo'?compose.yesNo.click():compose.multiple.click()
		if(question) compose.question= question
		if(!enableMessage) compose.dontSendQuestion.click()
		next.click()
	}
	
	def goToTab(tab) {
		at PollDialog
		tab(tab).click()
	}

	def setAliases(aliasValues=null) {
		at PollDialog
		tab(3).click()
		if (!aliasValues)
			aliasValues = ['A', 'B', 'C', 'D', 'E']
		aliasValues.eachWithIndex { alias, index ->
			if (aliases.inputs[index].displayed && !aliases.inputs[index].disabled) {
				aliases.inputs[index].value(alias)
			}
		}
	}
}

