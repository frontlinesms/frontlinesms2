package frontlinesms2.message

import frontlinesms2.*
import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class PollSpec extends frontlinesms2.poll.PollGebSpec {
	def 'message from alice is first in the list, and links to the show page'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
			def poll = Poll.findByTitle('Football Teams')
		when:
			to PollShowPage
			println $('#messages tbody tr:nth-child(2) a').text()
			def firstMessageLink = $('#messages tbody tr:nth-child(2) a', href:"/frontlinesms2/message/poll/${poll.id}/show/${message.id}")
		then:
			firstMessageLink.text() == 'Alice'
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}

	def 'selected message and its details are displayed'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
			def poll = Poll.findByTitle('Football Teams')
		when:
			to PollShowPage
			def formatedDate = dateToString(message.dateCreated)
		then:
			$('#message-details p:nth-child(1)').text() == message.src
			$('#message-details p:nth-child(3)').text() == formatedDate
			$('#message-details p:nth-child(4)').text() == message.text
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}

	def 'selected message is highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
			def poll = Poll.findByTitle('Football Teams')
			def aliceMessage = Fmessage.findBySrc('Alice')
			def bobMessage = Fmessage.findBySrc('Bob')
		when:
			to PollShowPage
		then:
			$('#messages .selected a').getAttribute('href') == "/frontlinesms2/message/poll/${poll.id}/show/${aliceMessage.id}"
		when:
			go "message//poll/${poll.id}/show/${bobMessage.id}"
		then:
			$('#messages .selected a').getAttribute('href') == "/frontlinesms2/message/poll/${poll.id}/show/${bobMessage.id}"
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}

	String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat();
		return formatedDate.format(date)
	}

	DateFormat createDateFormat() {
		return new SimpleDateFormat("dd-MMM-yyyy hh:mm")
	}
}

class PollShowPage extends geb.Page {
	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Alice').id}" }
	static at = {
		title.endsWith('Poll')
	}
}

