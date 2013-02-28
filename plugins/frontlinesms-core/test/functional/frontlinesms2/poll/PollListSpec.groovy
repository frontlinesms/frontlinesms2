package frontlinesms2.poll

import frontlinesms2.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessage
import frontlinesms2.page.PageMessageActivity

class PollListSpec extends PollBaseSpec {
	def "poll message list is displayed"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
		then:
			messageList.messageSource(0) == 'Alice'
			messageList.messageSource(1) == 'Bob'
	}

	def "message's poll details are shown in list"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams', Fmessage.findBySrc('Bob').id
		then:
			messageList.messageSource(1) == 'Bob'
			messageList.messageText(1) == 'manchester ("I like manchester")'
			messageList.messageDate(1)
	}

	def "poll details are shown in header and graph is displayed"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
		then:
			header.title == 'football teams poll'
			statsLabels == ['manchester', 'barcelona','Unknown']
			statsNumbers == ['2', '0', '0']
			statsPercents == ['100%', '0%', '0%']
		when:
			pollGraphBtn.click()
		then:
			waitFor { pollGraph.displayed}
	}

	def 'selected poll should be highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
		then:
			bodyMenu.selected == 'football teams poll'
	}

	def "should filter poll response messages for starred and unstarred messages"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
		then:
			messageList.messageCount() == 2
		when:
			footer.showStarred.click()
			waitFor { messageList.messageCount() == 1 }
		then:
			messageList.messageSource(0) == 'Bob'
		when:
			footer.showAll.click()
			waitFor { messageList.messageCount() == 2 }
		then:
			messageList.messageSource(0) == 'Alice'
			messageList.messageSource(1) == 'Bob'
	}

	def "should display message details when message is checked"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
			messageList.toggleSelect(1)
		then:
			waitFor { singleMessageDetails.text == "I like manchester" }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { multipleMessageDetails.checkedMessageCount == "2 messages selected" }
		when:
			messageList.toggleSelect(1)
			def message = Fmessage.findBySrc('Alice')
		then:
			waitFor { singleMessageDetails.sender == message.src }
			singleMessageDetails.text == message.text
	}

	def "should hide the messages when poll detail chart is shown"() {
		setup:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
		then:
			waitFor { messageList.displayed}
		when:
			pollGraphBtn.click()
		then:
			waitFor {pollGraph.displayed}
			!messageList.displayed
		when:
			pollGraphBtn.click()
		then:
			waitFor { !pollGraph.displayed }
	}
	
	def 'no message is selected when a poll is first loaded'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
		then:
			singleMessageDetails.text() == "No message selected"
	}

	def 'new messages are checked for in the backround every ten seconds and cause a notification to appear if there are new messages'() {
		when:
			createTestPolls()
			createTestMessages()
			to PageMessagePoll, 'Football Teams'
		then:
			messageList.messageCount == 2
			!messageList.newMessageNotification.displayed
		when:
			createMoreTestMessages()
		then:
			waitFor('very slow') { messageList.newMessageNotification.displayed }
	}
}

