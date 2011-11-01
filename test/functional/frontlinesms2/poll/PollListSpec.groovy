package frontlinesms2.poll

import frontlinesms2.*

class PollListSpec extends PollBaseSpec {
	def 'poll message list is displayed'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
			def pollMessageSources = $('#messages tbody tr td:nth-child(3)')*.text()
		then:
			at PageMessagePollFootballTeamsBob
			pollMessageSources == ['Alice', 'Bob']
	}

	def "message's poll details are shown in list"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
//			to PageMessagePollFootballTeamsBob
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
			def rowContents = $('#messages tbody tr:nth-child(2) td')*.text()
		then:
			rowContents[2] == 'Bob'
			rowContents[3] == 'manchester ("I like manchester")'
			rowContents[4] ==~ /[0-9]{2} [A-Z][a-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2}/
	}

	def "poll details are shown in header and graph is displayed"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
//			to PageMessagePollFootballTeamsBob
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
			def pollTitle = $("#poll-title div h2").text()
			def statsLabels = $('#poll-stats tbody tr td:first-child')*.text()
			def statsNums = $('#poll-stats tbody tr td:nth-child(2)')*.text()
			def statsPercents = $('#poll-stats tbody tr td:nth-child(3)')*.text()
		then:
			pollTitle == 'Football Teams poll'
			statsLabels == ['manchester', 'barcelona','Unknown']
			statsNums == ['2', '0', '0']
			statsPercents == ['(100%)', '(0%)', '(0%)']
		when:
			$("#pollSettings").click()
			waitFor {$('#pollGraph svg').displayed}
		then:
			$('#pollGraph svg')
	}

	def 'selected poll should be highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
//			to PageMessagePollFootballTeamsBob
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
		then:
			$('#messages-menu .selected').text() == 'Football Teams'
	}

	def "should filter poll response messages for starred and unstarred messages"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
//			to PageMessagePollFootballTeamsBob
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
			waitFor {$("#messages tbody tr").size() == 1}
		then:
			$("#messages tbody tr")[0].find("td:nth-child(3)").text() == 'Bob'
		when:
			$('a', text:'All').click()
			waitFor {$("#messages tbody tr").size() == 2}
		then:
			$("#messages tbody tr").collect {it.find("td:nth-child(3)").text()}.containsAll(['Bob', 'Alice'])
	}

	def "should only display message details when one message is checked"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
//			to PageMessagePollFootballTeamsBob
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
			$(".message-select")[2].click()
		then:
			waitFor { $('#message-body').text() == 'I like manchester' }
		when:
			$(".message-select")[1].click()
		then:
			waitFor { $("#checked-message-count").text() == "2 messages selected" }
		when:
			$(".message-select")[2].click()
			def message = Fmessage.findBySrc('Alice')
		then:
			waitFor { $('#message-details #contact-name').text() == message.src }
			$('#message-details #message-body').text() == message.text
	}

	def "should hide the messages when poll detail chart is shown"() {
		setup:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Alice').id}"
			waitFor {$("#messages").displayed}
		then:
			$("#messages").displayed
		when:
			$("#pollSettings").click()
			waitFor {!$("#messages").displayed}
		then:
			$(".response-count").text() == "2 responses total"
		when:
			$("#pollSettings").click()
		then:
			$("#messages").displayed
	}
	
	def 'no message is selected when a poll is first loaded'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}"
		then:
			$('#message-details #message-body').text() == "No message selected"
	}
}
