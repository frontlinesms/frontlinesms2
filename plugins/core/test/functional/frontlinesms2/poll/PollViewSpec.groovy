package frontlinesms2.poll

import frontlinesms2.*
import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class PollViewSpec extends PollBaseSpec {
	
	private def DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US)
	
	def 'existing polls appear in activities section of messages'() {
		given:
			createTestPolls()
		when:
			go 'message'
		then:
			$('#activities-submenu li')*.text().containsAll('Football Teams poll', 'Shampoo Brands poll', 'Rugby Brands poll')
	}
	
	def 'message from bob is first in the list, and links to the show page'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Bob')
			def poll = Poll.findByName('Football Teams')
		when:
			go "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
		then:
			$('#message-list tr a', text: "Bob").displayed
	}

	def 'selected message and its details are displayed'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			go "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc("Alice").id}"
		then:
			$('#message-detail #message-detail-sender').text() == message.src
			$('#message-detail #message-detail-date').text() == DATE_FORMAT.format(message.date)
			$('#message-detail #message-detail-content').text() == message.text
	}

	def 'selected message is highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
			def poll = Poll.findByName('Football Teams')
			def aliceMessage = Fmessage.findBySrc('Alice')
			def bobMessage = Fmessage.findBySrc('Bob')
		when:
			go "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc("Alice").id}"
		then:
			$('#message-list .selected a')[3].@href == "/message/activity/$poll.id/show/$aliceMessage.id"
		when:
			go "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
		then:
			$('#message-list .selected a', text: "Bob").displayed
	}
}
