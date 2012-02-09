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
			$('#activities-submenu li')[0].text().contains('Football Teams') // TODO: find/implement array contains
			$('#activities-submenu li')[1].text().contains('Shampoo Brands')
			$('#activities-submenu li')[2].text().contains('Rugby Brands')
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
			$('#messages tbody a', text: "Bob").displayed
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
			$('#messages .selected a')[3].@href == "/message/activity/$poll.id/show/$aliceMessage.id?viewingArchive="
		when:
			go "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
		then:
			$('#messages .selected a', text: "Bob").displayed
	}
	
	def 'activities should also list message counts'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
//			to PageMessagePollFootballTeamsBob
			go "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
		then:
			$('#activities-submenu li')[0..2]*.text() == ['Football Teams poll', 'Shampoo Brands poll', 'Rugby Brands poll']
	}
	
}