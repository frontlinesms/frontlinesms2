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
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
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
			rowContents[4] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}

	def "poll details are shown in header and graph is displayed"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Bob').id}"
			def pollTitle = $("#poll-header #message-title h2").text()
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
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}

	def 'selected poll is highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Bob').id}"
		then:
			selectedMenuItem.text() == 'Football Teams'
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
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
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}
	
	def "should only display message details when one message is checked"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Alice').id}"
			$("#message")[1].click()
			$("#message")[2].click()
		then:
			$('#message-details #message-count').text() == "2 messages selected"
		when:
			$("#message")[1].click()
			def message = Fmessage.findBySrc('Bob')
		then:
			$('#message-details .message-name').text() == message.src
			$('#message-details #message-body').text() == message.text
		
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}

	def "should display message count when multiple messages are selected"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Bob').id}"
			$("#message")[1].click()
			$("#message")[2].click()
		then:
			$('#message-details #message-count').text() == "2 messages selected"
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}
	
	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createTestPolls()
			createTestMessages()
			new Contact(name: 'June', primaryMobile: '+2544635263').save(failOnError:true)
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Bob').id}"
			$("#message")[1].click()
			$("#message")[2].click()
			waitFor {$('#message-details div.buttons').text().contains("Reply All")}
			def btnReply = $('#message-details div.buttons a')[0]
		then:
			btnReply
		when:
			btnReply.click()
			waitFor {$('div#tabs-1').displayed}
			$("div#tabs-1 .next").click()
		then:
			$('a', text:'Alice').parent().previous().previous().getAttribute('checked')
			$('a', text:'Bob').parent().previous().previous().getAttribute('checked')
			
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
			deleteTestContacts()
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
