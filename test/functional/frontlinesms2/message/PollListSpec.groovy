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
			pollMessageSources == ['Bob', 'Alice']
		cleanup:
			deleteTestMessages()
			deleteTestPolls()
	}

	def "message's poll details are shown in list"() {
		given:
			createTestPolls()
			createTestMessages()
			assert Poll.count() == 2
		when:
			to PollListPage
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			println $('#messages').text()
			rowContents[0] == 'Bob'
			rowContents[1] == 'manchester ("I like manchester")'
			rowContents[2] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
		cleanup:
			deleteTestMessages()
			deleteTestPolls()
	}

	def "poll details are shown in header"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollListPage
			def pollTitle = $('#poll-title').text()
//			def pollQuestion = $('#poll-question p').text()
			def statsLabels = $('#poll-stats tbody tr td:first-child')*.text()
			def statsNums = $('#poll-stats tbody tr td:nth-child(2)')*.text()
			def statsPercents = $('#poll-stats tbody tr td:nth-child(3)')*.text()
		then:
			pollTitle == 'Football Teams'
			statsLabels == ['barcelona', 'manchester']
			statsNums == ['0', '2']
			statsPercents == ['(0%)', '(100%)']
		cleanup:
			deleteTestMessages()
			deleteTestPolls()
	}

	def 'selected poll is highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollListPage
		then:
			selectedMenuItem.text() == 'Football Teams'
	}
}

class PollListPage extends geb.Page {
 	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}" }
	static at = {
		title.endsWith('Poll')
	}
	static content = {
		selectedMenuItem { $('#messages-menu .selected') }
		messagesList { $('#messages-submenu') }
	}
}


