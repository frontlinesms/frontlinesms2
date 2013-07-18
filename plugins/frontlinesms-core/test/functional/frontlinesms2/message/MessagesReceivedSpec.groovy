package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.utils.*

class MessagesReceivedSpec extends MessageBaseSpec {
	def 'new messages in the current section are checked for in the backround every ten seconds and causes a notification to appear if there are new messages'() {
		when:
			createInboxTestMessages()
			to PageMessageInbox
		then:
			messageList.messageCount() == 2
			!messageList.newMessageNotification.displayed
		when:
			createTestMessages()
		then:
			messageList.messageCount() == 2
			!messageList.newMessageNotification.displayed
			waitFor { messageList.newMessageNotification.displayed }
	}

	def 'clicking the new message notification refreshes the list and removes the notification'() {
		when:
			createInboxTestMessages()
			to PageMessageInbox
		then:
			messageList.messageCount() == 2
		when:
			createTestMessages()
		then:
			waitFor { messageList.newMessageNotification.displayed }
			messageList.messageCount() == 3
		when:
			messageList.newMessageNotification.find("a").click()
		then:
			waitFor { messageList.messageCount() == 5 }
			!messageList.newMessageNotification.displayed
	}

	def 'when clicking the new message notification, the view stays at the current page and details'() {
		when:
			createInboxTestMessages()
			to PageMessageInbox, remote { Fmessage.findBySrc('Bob').id }
		then:
			messageList.messageCount() == 2
		when:
			createTestMessages()
		then:
			waitFor('very-slow') { messageList.newMessageNotification.displayed }
		when:
			messageList.newMessageNotification.find("a").click()
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.sender == "Bob" }
	}
}

