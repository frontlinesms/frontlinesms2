package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.utils.*

class MessagesRecievedSpec extends MessageBaseSpec {
	
	def 'new messages in the current section are checked for in the backround every ten seconds and causes a notification to appear if there are new messages'() {
		when:
			createInboxTestMessages()
			go 'message/inbox'
		then:
			at PageMessageInbox
			visibleMessageTotal == 2
		when:
			sleep 11000
		then:
			visibleMessageTotal == 2
			!newMessageNotification.displayed
		when:
			createTestMessages()
			sleep 5000
		then:
			visibleMessageTotal == 2
			!newMessageNotification.displayed
		when:
			sleep 5000
		then:
			waitFor { newMessageNotification.displayed }
	}
	
	def 'clicking the new message notification refreshes the list and removes the notification'() {
		when:
			createInboxTestMessages()
			go 'message/inbox'
		then:
			at PageMessageInbox
			visibleMessageTotal == 2
		when:
			createTestMessages()
			sleep 11000
		then:
			waitFor { newMessageNotification.displayed }
			visibleMessageTotal == 3
		when:
			newMessageNotification.find("a").click()
		then:
			waitFor { visibleMessageTotal == 5 }
			!newMessageNotification.displayed
	}
		
	def 'when clicking the new message notification, the view stays at the current page and details'() {
		when:
			createInboxTestMessages()
			go "message/inbox/show/${Fmessage.findBySrc('Bob').id}"
		then:
			at PageMessageInboxBob
			visibleMessageTotal == 2
		when:
			createTestMessages()
			sleep 11000
		then:
			waitFor { newMessageNotification.displayed }
		when:
			newMessageNotification.click()
		then:
			at PageMessageInboxBob
	}
}
