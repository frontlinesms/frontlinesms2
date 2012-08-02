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
			messageList.sources.containsAll('Alice', 'Bob')
	}

	def "message's poll details are shown in list"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams', Fmessage.findBySrc('Bob').id
		then:
			//rowContents[4] ==~ /[A-Za-z]{3,9} [0-9]{2}, [0-9]{4} [0-9]{2}:[0-9]{2} [A-Z]{2}/
			messageList.messages[1].source == 'Bob'
			messageList.messages[1].text == 'manchester ("I like manchester")'
			messageList.messages[1].date ==~ /[0-9]{2} [A-Za-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2} [A-Z]{2}/
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
			messageList.messages.size() == 2
		when:
			footer.showStarred.click()
			waitFor { messageList.messages.size() == 1 }
		then:
			messageList.sources == ['Bob']
		when:
			footer.showAll.click()
			waitFor {messageList.messages.size() == 2}
		then:
			messageList.sources == ['Alice', 'Bob']
	}

	def "should only display message details when one message is checked"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
			messageList.messages[1].checkbox.click()
		then:
			waitFor { singleMessageDetails.text == "I like manchester" }
		when:
			messageList.messages[0].checkbox.click()
		then:
			waitFor { multipleMessageDetails.checkedMessageCount == "2 messages selected" }
		when:
			$(".message-select")[2].click()
			def message = Fmessage.findBySrc('Alice')
		then:
			waitFor { $('#message-detail #message-detail-sender').text() == message.src }
			$('#message-detail #message-detail-content').text() == message.text
	}

	def "should hide the messages when poll detail chart is shown"() {
		setup:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePollFootballTeamsBob
		then:
			waitFor { $("#message-list").displayed}
		when:
			$("#poll-graph-btn").click()
		then:
			waitFor {$("#poll-details").displayed}
			$(".response-count").text() == "2 responses total"
		when:
			$("#poll-graph-btn").click()
		then:
			waitFor { !$('#poll-details').displayed }
	}
	
	def 'no message is selected when a poll is first loaded'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/activity/${Poll.findByName('Football Teams').id}"
		then:
			$('#message-detail #message-detail-content').text() == "No message selected"
	}
	
	def 'new messages are checked for in the backround every ten seconds and cause a notification to appear if there are new messages'() {
		when:
			createTestPolls()
			createTestMessages()
			to PageMessagePollFootballTeamsAlice
		then:
			visibleMessageTotal == 3
		when:
			sleep 11000
		then:
			visibleMessageTotal == 3
			!newMessageNotification.displayed
		when:
			createMoreTestMessages()
			sleep 5000
		then:
			visibleMessageTotal == 3
			!newMessageNotification.displayed
		when:
			sleep 7000
		then:
			waitFor { newMessageNotification.displayed }
	}
}
