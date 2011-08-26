package frontlinesms2.message

import frontlinesms2.*

class PollListSpec extends frontlinesms2.poll.PollGebSpec {
	def 'poll message list is displayed'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollListPage
			def pollMessageSources = $('#messages tbody tr td:nth-child(3)')*.text()
		then:
			at PollListPage
			pollMessageSources == ['Alice', 'Bob']
	}

	def "message's poll details are shown in list"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Bob').id}"
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
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Bob').id}"
			def pollTitle = $("#poll-title div h2").text()
			def statsLabels = $('#poll-stats tbody tr td:first-child')*.text()
			def statsNums = $('#poll-stats tbody tr td:nth-child(2)')*.text()
			def statsPercents = $('#poll-stats tbody tr td:nth-child(3)')*.text()
		then:
			pollTitle == 'Football Teams'
			statsLabels == ['manchester', 'barcelona','Unknown']
			statsNums == ['2', '0', '0']
			statsPercents == ['(100%)', '(0%)', '(0%)']
		when:
			$("#pollSettings").click()
			waitFor {$('#pollGraph svg').displayed}
		then:
			$('#pollGraph svg')
	}

	def 'selected poll is highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Bob').id}"
		then:
			selectedMenuItem.text() == 'Football Teams'
	}

	def "should filter poll response messages for starred and unstarred messages"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Bob').id}"
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
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Alice').id}"
			$("#message")[1].click()
			$("#message")[2].click()
			sleep 1000
		then:
			$("#checked-message-count").text() == "2 messages selected"
		when:
			$("#message")[1].click()
			sleep 1000
			def message = Fmessage.findBySrc('Bob')
		then:
			$('#message-details #contact-name').text() == message.src
			$('#message-details #message-body').text() == message.text
	}

	def "should remain in the same page when all archived poll messages are deleted"() {
		setup:
			createTestPolls()
			createTestMessages()
			def archivedPoll = new Poll(title: "archived poll", archived: true)
			archivedPoll.addToResponses(new PollResponse(value: "response1", key:"A"))
			archivedPoll.addToResponses(new PollResponse(value: "response2", key:"B"))
			archivedPoll.save(flush: true)
			[PollResponse.findByValue('response1').addToMessages(Fmessage.findBySrc('Bob')),
					PollResponse.findByValue('response1').addToMessages(Fmessage.findBySrc('Alice')),
					PollResponse.findByValue('response2').addToMessages(Fmessage.findBySrc('Joe'))]*.save(failOnError:true, flush:true)
		when:
			$("#global-nav a", text: "Archive").click()
			def activityArchiveButton = $("a", text: 'Activity archive')
			waitFor{activityArchiveButton.displayed}
			activityArchiveButton.click()
			waitFor{$("a", text:'archived poll').displayed}
			$("a", text:'archived poll').click()
			waitFor {$("#messages").displayed}
			$("#message")[0].click()
			sleep(1000)
			waitFor {$('#multiple-messages').displayed}
			def btnDelete = $('#multiple-messages a')[1]
		then:
			btnDelete
		when:
			btnDelete.click()
			sleep(2000)
			waitFor() {$("div.flash").text().contains("messages deleted") }
		then:
			$("#global-nav a", text: "Archive").hasClass("selected")
	}
}

class PollListPage extends geb.Page {
 	static url = "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Bob').id}"
	static at = {
		title.endsWith('Poll')
	}
	static content = {
		selectedMenuItem { $('#messages-menu .selected') }
		messagesList { $('#messages-submenu') }
	}
}
