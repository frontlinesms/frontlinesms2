package frontlinesms2.poll

import frontlinesms2.*
import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class PollViewSpec extends PollBaseSpec {
	
	private def DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyyy hh:mm", Locale.US)
	
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
			def poll = Poll.findByTitle('Football Teams')
		when:
			to PageMessagePollFootballTeamsBob
			def firstMessageLink = $('#messages tbody tr:nth-child(1) a', href:"/frontlinesms2/message/poll/$poll.id/show/$message.id")
		then:
			firstMessageLink.text() == 'Bob'
	}

	def 'selected message and its details are displayed'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			to PageMessagePollFootballTeamsAlice
		then:
			$('#message-details #contact-name').text() == message.src
			$('#message-details #message-date').text() == DATE_FORMAT.format(message.dateCreated)
			$('#message-details #message-body').text() == message.text
	}

	def 'selected message is highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
			def poll = Poll.findByTitle('Football Teams')
			def aliceMessage = Fmessage.findBySrc('Alice')
			def bobMessage = Fmessage.findBySrc('Bob')
		when:
			to PageMessagePollFootballTeamsAlice
		then:
			$('#messages .selected td:nth-child(3) a').@href == "/frontlinesms2/message/poll/$poll.id/show/$aliceMessage.id"
		when:
			//go "message/poll/$poll.id/show/$bobMessage.id"
			to PageMessagePollFootballTeamsBob
		then:
			$('#messages .selected td:nth-child(3) a').@href == "/frontlinesms2/message/poll/$poll.id/show/$bobMessage.id"
	}
	
	def 'activities should also list message counts'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePollFootballTeamsBob
		then:
			$('#activities-submenu li')[0..2]*.text() == ['Football Teams', 'Shampoo Brands', 'Rugby Brands']
	}
	
}