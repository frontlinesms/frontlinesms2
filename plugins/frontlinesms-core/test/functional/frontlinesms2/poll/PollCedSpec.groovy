package frontlinesms2.poll

import frontlinesms2.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.message.PageMessagePending
import frontlinesms2.message.PageMessageTrash
import frontlinesms2.popup.*
import java.util.regex.*

class PollCedSpec extends PollBaseSpec {
	
	def "entering correct url will load poll"() {
		// also a test of PageMessageActivity.convertToPath
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
		then:
			header.title == 'poll.title[football teams]'
		when:
			to PageMessagePoll, 'Football Teams', remote { TextMessage.findBySrc('Alice').id }
		then:
			header.title == 'poll.title[football teams]'
		when:
			to PageMessagePoll, remote { Poll.findByName('Football Teams').id }, 2
		then:
			header.title == 'poll.title[football teams]'
	}

	def "should auto populate poll response when a poll with yes or no answer is created"() {
		when:
			launchPollPopup('yesNo', 'question', false)
			setKeywords();
			tab(7).click()
		then:
			confirm.pollName.displayed
		when:
			confirm.pollName = "POLL NAME"
			submit.click()
		then:
			waitFor { summary.displayed }
			remote { Poll.findByName("POLL NAME").responses*.value.containsAll("Yes", "No", "Unknown") }
	}

	def "should require keywords if sorting is enabled"() {
		when:
			launchPollPopup('yesNo', 'question', false)
			sort.sort.jquery.click()
			sort.inputs[0].value("")
			sort.inputs[1].value("")
			next.click()
		then:
			waitFor { errorPanel.displayed }
			sort.inputs[0].hasClass('error')
			sort.inputs[1].hasClass('error')
	}

	def "Keyword input fields should be displayed when popup first loads"() {
		when:
			launchPollPopup('yesNo','question',false)
		then:
			sort.keyword.displayed
			sort.pollKeywordsContainer.displayed
	}

	def "should skip recipients tab when do not send message option is chosen"() {
		when:
			launchPollPopup('yesNo', 'question', false)
			setKeywords()
		then:
			waitFor { sort.displayed }
		when:
			sort.sort.click()
			sort.keyword.value("key")
			tab(7).click()
		then:
			waitFor { confirm.displayed }
			tab(2).hasClass("disabled-tab")
			tab(6).hasClass("disabled-tab")
		when:
			previous.click()
		then:
			waitFor { autoreply.displayed }
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
			setKeywords()
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
			waitFor { sort.displayed }
		when:
			sort.sort.click()
			sort.keyword = 'coffee'
			next.click()
			setKeywords()
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
			waitFor { error }
		when:
			recipients.addRecipient('1234567890')
		then:
			waitFor { recipients.count == 1 }
		when:
			next.click()
		then:
			waitFor { confirm.displayed }
			confirm.message == 'How often do you drink coffee? poll.reply.text1[poll.reply.text1[poll.reply.text1[poll.reply.text5,,COFFEE A,Never],,COFFEE B,Once a day],,COFFEE C,Twice a day].'
			confirm.recipientCount == '1 quickmessage.recipients.count'
			confirm.messageCount == "1 quickmessage.messages.count"
			confirm.autoreply == "Thanks for participating..."
		when:
			confirm.pollName = "Coffee Poll"
			submit.click()
		then:
			waitFor { remote { Poll.findByName("Coffee Poll")?.id } }
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
			next.click()
		then:
			waitFor { sort.displayed }
		when:
			sort.sort.click()
			sort.keyword = 'coffee'
			next.click()
			setKeywords()
			next.click()
		then:
			waitFor { autoreply.displayed }
		when:
			next.click()
		then:
			waitFor { edit.displayed }
			edit.text.jquery.val() == 'How often do you drink coffee?\npoll.reply.text1[poll.reply.text1[poll.reply.text1[poll.reply.text5,,COFFEE A,Never],,COFFEE B,Once a day],,COFFEE C,Twice a day].'
		when:
			edit.text.value('How often do you drink coffee? Reply "COFFEE A" for Never, "COFFEE B" for Once a day, "COFFEE C" for Twice a day. Thanks for participating')
			next.click()
		then:
			waitFor { recipients.displayed }
		when:
			recipients.addRecipient '1234567890'
		then:
			waitFor { recipients.count == 1 }
		when:
			next.click()
		then:
			waitFor { confirm.displayed }
			confirm.message == 'How often do you drink coffee? Reply "COFFEE A" for Never, "COFFEE B" for Once a day, "COFFEE C" for Twice a day. Thanks for participating'
			confirm.recipientCount == '1 quickmessage.recipients.count'
			confirm.messageCount == "1 quickmessage.messages.count"
		when:
			confirm.pollName = "Coffee Poll"
			submit.click()
		then:
			waitFor { remote { Poll.findByName("Coffee Poll")?.id } }
	}

	def "should update confirm screen when user decides not to send messages"() {
		when:
			launchPollPopup('yesNo', "Will you send messages to this poll", false)
		then:
			waitFor { sort.displayed }
		when:
			setKeywords()
			tab(7).click()
		then:
			waitFor { confirm.displayed }
			confirm.recipientCount == "0 quickmessage.recipients.count"
	}

	def "can launch export popup"() {
		when:
			def pollId = remote {
				def p = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
				p.addToResponses(key: 'A', value: 'Michael-Jackson')
				p.addToResponses(key: 'B', value: 'Chuck-Norris')
				p.addToResponses(key: 'Unknown', value: 'Unknown')
				p.save(failOnError:true, flush:true)
				p.id
			}
			to PageMessageInbox
		then:
			at PageMessageInbox
		when:	
			bodyMenu.activityLink("Who is badder?").click()
		then:
			waitFor { header.title == 'poll.title[who is badder?]' }
			at PageMessagePoll
		when:
			moreActions.value("export").click()
		then:	
			waitFor { at ExportDialog }
	}

	def "can rename a poll"() {
		when:
			def pollId = remote {
				def p = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
				p.addToResponses(key: 'A', value: 'Michael-Jackson')
				p.addToResponses(key: 'B', value: 'Chuck-Norris')
				p.addToResponses(key: 'Unknown', value: 'Unknown')
				p.save(failOnError:true, flush:true)
				p.id
			}
			to PageMessageInbox
		then:
			at PageMessageInbox
		when:	
			bodyMenu.activityLink("Who is badder?").click()
		then:
			waitFor { header.title == 'poll.title[who is badder?]' }
			at PageMessagePoll
		when:
			moreActions.value("rename").click()
		then:
			waitFor { at RenameDialog }
		when:
			name.value("rename poll")
			done.click()
		then:
			at PageMessageInbox
			waitFor { header.title == 'poll.title[rename poll]' }
	}

	def "can delete a poll"() {
		when:
			deletePoll()
		then:
			at PageMessageInbox
			waitFor { notifications.flashMessage.displayed }
			bodyMenu.activityLinks.size() == 1
	}

	def "deleted polls show up in the trash section"() {
		setup:
			def pollId = deletePoll()
		when:
			to PageMessageTrash, remote { Trash.findByObjectId(pollId).id }
		then:
			messageList.messageCount == 1
			messageList.messageSource == 'Who is badder?'
			messageList.messageText == '0 message(s)'
	}

	def "selected poll and its details are displayed"() {
		setup:
			def pollId = deletePoll()
			def pollName = remote { Poll.get(pollId).name }
			def msgCount = remote { Poll.get(pollId).getLiveMessageCount() }
		when:
			to PageMessageTrash, remote { Trash.findByObjectId(pollId).id }
		then:
			messageList.messageCount == 1
			messageList.messageSource == pollName
			messageList.messageText == "${msgCount} message(s)"
			messageList.messageDate
	}

	def "clicking on empty trash permanently deletes a poll"() {
		setup:
			deletePoll()
		when:
			to PageMessageTrash
			trashMoreActions.value("empty-trash")
		then:
			waitFor { at EmptyTrashPopup }
		when:
			ok.click()
		then:
			remote  { Poll.findAll().size() == 0 }
	}

	def "user can edit an existing poll"() {
		setup:
			def pollId = remote {
				def p = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
				p.addToResponses(key: 'A', value: 'Michael-Jackson', aliases:'A')
				p.addToResponses(key: 'B', value: 'Chuck-Norris', aliases:'B')
				p.addToResponses(PollResponse.createUnknown())
				p.save(failOnError:true, flush:true)
				p.refresh()
				p.id
			}
		when:
			to PageMessageInbox
			bodyMenu.activityLinks[0].click()
		then:
			waitFor { at PageMessagePoll }
		when:
			moreActions.value("edit").click()
		then:
			waitFor('slow') { at EditPollDialog }
			compose.question == 'question'
			compose.pollType == "multiple"
		when:
			next.click()
		then:
			waitFor { response.displayed }
			response.choice("A").jquery.val() == "Michael-Jackson"
			response.choice("B").jquery.val() == "Chuck-Norris"
		when:
			response.choice("C").jquery.val("Bruce Vandam")
			response.choice("C").jquery.trigger("keyup")
			next.click()
		then:
			waitFor { sort.displayed }
		when:
			sort.dontSort.click()
			next.click()
			setKeywords()
			goToTab(6)
			recipients.addRecipient '1234567890'
		then:
			waitFor { recipients.count == 1 }
		when:
			next.click()
			confirm.pollName = 'Who is badder?'
			submit.click()
		then:
			waitFor { summary.displayed }
			waitFor { remote { Poll.findByName("Who is badder?").responses*.value.containsAll("Michael-Jackson", "Chuck-Norris", "Bruce Vandam") } }
	}
	
	def "should display errors when poll validation fails"() {
		given:
			def pollId = remote {
				def p = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
				p.addToResponses(key: 'A', value: 'Michael-Jackson')
				p.addToResponses(key: 'B', value: 'Chuck-Norris')
				p.addToResponses(key: 'Unknown', value: 'Unknown')
				p.save(failOnError:true, flush:true)
				p.id
			}
			remote { assert Poll.count() == 1 }
			
		when:
			launchPollPopup('yesNo', 'question', false)
		then:
			waitFor { sort.displayed }
			setKeywords()
		when:
			tab(7).click()
			confirm.pollName = 'Who is badder?'
			submit.click()
		then:
			remote { Poll.count() == 1 }
			at PollDialog
			waitFor { errorPanel.displayed }
	}

	def "Choices for a saved poll should validate as required"() {
		setup:
			def pollId = remote {
				def p = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
				p.addToResponses(key: 'A', value: 'Michael-Jackson')
				p.addToResponses(key: 'B', value: 'Chuck-Norris')
				p.addToResponses(key: 'C', value: 'Michael Jordan')
				p.addToResponses(key: 'D', value: 'Bart Simpson')
				p.addToResponses(key: 'Unknown', value: 'Unknown')
				p.save(failOnError:true, flush:true)
				p.refresh()
				p.id
			}
		when:
			to PageMessagePoll, pollId
		then:
			moreActions.value("edit").click()
		when:
			waitFor('slow') { at EditPollDialog }
			next.click()
		then:
			response.choice("C").jquery.val("")
			response.choice("C").jquery.trigger('keyup')
		when:
			next.click()
		then:
			response.choice("C").hasClass("error")
			response.errorLabel('C').text() == 'poll.choice.validation.error.deleting.response'
	}

	def deletePoll() {
		def pollId = remote {
			def poll = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
			poll.addToResponses(key: 'A', value: 'Michael-Jackson')
			poll.addToResponses(key: 'B', value: 'Chuck-Norris')
			poll.addToResponses(key: 'Unknown', value: 'Unknown')
			poll.save(failOnError:true, flush:true)
			poll.id
		}
		to PageMessagePoll, pollId
		moreActions.value("delete")
		waitFor { at DeleteActivity }
		ok.click()
		pollId
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
	
	def goToTab(tabNo) {
		at PollDialog
		tab(tabNo).click()
	}

	def setKeywords(aliasValues=null) {
		at PollDialog
		tab(3).click()
		if (!aliasValues)
			aliasValues = ['A', 'B', 'C', 'D', 'E']
		aliasValues.eachWithIndex { alias, index ->
			if (sort.inputs[index].displayed && !sort.inputs[index].disabled) {
				sort.inputs[index].value(alias)
			}
		}
	}
}
