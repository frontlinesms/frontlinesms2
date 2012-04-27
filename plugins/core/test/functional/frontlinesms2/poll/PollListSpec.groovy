package frontlinesms2.poll

import frontlinesms2.*

class PollListSpec extends PollBaseSpec {
	def "poll message list is displayed"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePollFootballTeamsBob
			def pollMessageSources = $('#message-list .message-sender-cell a')*.text()
		then:
			pollMessageSources.containsAll('Alice', 'Bob')
	}

	def "message's poll details are shown in list"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePollFootballTeamsBob
			def rowContents = $('#message-list .main-table tr:nth-child(3) td')*.text()
		then:
			rowContents[2] == 'Bob'
			rowContents[3] == 'manchester ("I like manchester")'
			rowContents[4] ==~ /[0-9]{2} [A-Z][a-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2} [A-Z]{2}/
	}

	def "poll details are shown in header and graph is displayed"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePollFootballTeamsBob
			def pollTitle = $("h3.activity").text()
			def statsLabels = $('#poll-stats tbody tr td:first-child')*.text()
			def statsNums = $('#poll-stats tbody tr td:nth-child(2)')*.text()
			def statsPercents = $('#poll-stats tbody tr td:nth-child(3)')*.text()
		then:
			pollTitle.equalsIgnoreCase('Football Teams poll')
			statsLabels == ['manchester', 'barcelona','Unknown']
			statsNums == ['2', '0', '0']
			statsPercents == ['100%', '0%', '0%']
		when:
			$("#poll-graph-btn").click()
		then:
			waitFor {$('#pollGraph').displayed}
	}

	def 'selected poll should be highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePollFootballTeamsBob
		then:
			$('#sidebar .selected').text() == 'Football Teams poll'
	}

	def "should filter poll response messages for starred and unstarred messages"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePollFootballTeamsBob
		then:
			$("#message-list .main-table tr").size() == 3
		when:
			$('a', text:'Starred').click()
			waitFor { $("#message-list .main-table tr").size() == 2 }
		then:
			$("#message-list .main-table tr")[1].find(".message-sender-cell").text() == 'Bob'
		when:
			$('a', text:'All').click()
			waitFor {$("#message-list .main-table tr").size() == 3}
		then:
			$("#message-list .main-table tr").collect {it.find(".message-sender-cell").text()}.containsAll(['Bob', 'Alice'])
	}

	def "should only display message details when one message is checked"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePollFootballTeamsBob
			$(".message-select")[2].click()
		then:
			waitFor { $('#message-detail-content').text() == 'I like manchester' }
		when:
			$(".message-select")[1].click()
		then:
			waitFor { $("#checked-message-count").text() == "2 messages selected" }
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
			waitFor {!$("#message-list").displayed}
			$(".response-count").text() == "2 responses total"
		when:
			$("#poll-graph-btn").click()
		then:
			waitFor { $('#messages').displayed }
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
