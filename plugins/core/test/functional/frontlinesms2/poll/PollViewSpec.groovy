package frontlinesms2.poll

import frontlinesms2.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.page.PageMessageActivity
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
			to PageMessageInbox
		then:
			bodyMenu.activityList*.text() == ['Football Teams poll', 'Shampoo Brands poll', 'Rugby Brands poll', 'Create new activity']
	}

	def 'message from bob is second in the list, and links to the show page'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Bob')
			def poll = Poll.findByName('Football Teams')
		when:
			to PageMessagePoll, 'Football Teams', message.id
		then:
			messageList.messages[1].source == 'Bob'
	}

	def 'selected message and its details are displayed'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			to PageMessagePoll, 'Football Teams', message.id
		then:
			messageList.messages[0].source == message.src
			messageList.messages[0].dateCell ==~ /[0-9]{2} [A-Za-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2} [A-Z]{2}/
			messageList.messages[0].text == 'manchester ("go manchester")'
	}

	def 'selected message is highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
			def poll = Poll.findByName('Football Teams')
			def aliceMessage = Fmessage.findBySrc('Alice')
			def bobMessage = Fmessage.findBySrc('Bob')
		when:
			to PageMessagePoll, 'Football Teams', aliceMessage.id
		then:
			messageList.selectedMessages.text == ['manchester ("go manchester")']
		when:
			to PageMessagePoll, 'Football Teams', bobMessage.id
		then:
			messageList.selectedMessages.text == ['manchester ("I like manchester")']
	}
}
