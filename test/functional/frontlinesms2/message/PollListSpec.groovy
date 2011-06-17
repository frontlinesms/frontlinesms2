package frontlinesms2.message

import frontlinesms2.*

class PollListSpec extends frontlinesms2.poll.PollGebSpec {
	def 'poll message list is displayed'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollListPage
			def pollMessageSources = $('#messages tbody tr td:first-child')*.text()
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
			rowContents[0] == 'Bob'
			rowContents[1] == 'manchester ("I like manchester")'
			rowContents[2] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}

	def "poll details are shown in header"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Bob').id}"
			def pollTitle = $('#poll-title').text()
			def statsLabels = $('#poll-stats tbody tr td:first-child')*.text()
			def statsNums = $('#poll-stats tbody tr td:nth-child(2)')*.text()
			def statsPercents = $('#poll-stats tbody tr td:nth-child(3)')*.text()
		then:
			pollTitle == 'Football Teams'
			statsLabels == ['Unknown', 'manchester', 'barcelona']
			statsNums == ['0', '2', '0']
			statsPercents == ['(0%)', '(100%)', '(0%)']
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


