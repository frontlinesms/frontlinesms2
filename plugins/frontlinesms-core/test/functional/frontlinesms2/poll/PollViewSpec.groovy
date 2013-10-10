package frontlinesms2.poll

import frontlinesms2.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.page.PageMessageActivity
import java.util.regex.*

class PollViewSpec extends PollBaseSpec {
	def 'existing polls appear in activities section of messages'() {
		given:
			createTestPolls()
		when:
			to PageMessageInbox
		then:
			bodyMenu.activityList*.text().containsAll(['Football Teams poll','Shampoo Brands poll', 'Rugby Brands poll', 'Create new activity'])
	}

	def 'message from bob is second in the list, and links to the show page'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams', remote { Fmessage.findBySrc('Bob').id }
		then:
			messageList.messageSource(1) == 'Bob'
	}

	def 'selected message and its details are displayed'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams', remote { Fmessage.findBySrc('Alice').id }
		then:
			messageList.messageSource(0) == 'Alice'
			messageList.messageDate(0)
			messageList.messageText(0) == 'manchester ("go manchester")'
	}

	def 'selected message is highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams', remote { Fmessage.findBySrc('Alice').id }
		then:
			messageList.selectedMessageCount == 1
			messageList.selectedMessageText == 'manchester ("go manchester")'
		when:
			to PageMessagePoll, 'Football Teams', remote { Fmessage.findBySrc('Bob').id }
		then:
			messageList.selectedMessageCount == 1
			messageList.selectedMessageText == 'manchester ("I like manchester")'
	}
}

