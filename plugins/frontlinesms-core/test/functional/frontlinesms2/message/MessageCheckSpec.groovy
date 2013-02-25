package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.popup.*

import spock.lang.*

class MessageCheckSpec extends MessageBaseSpec {
	def "header checkbox is checked when all the messages are checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor { messageList.selectAll.checked }
	}

	def "message count displayed when multiple messages are selected"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor('veryslow') { multipleMessageDetails.checkedMessageCount == "2 messages selected"}
	}

	def "checked message details are displayed when message is checked"() {
		given:
			createInboxTestMessages()
			def m = Fmessage.findBySrc('Bob')
		when:
			to PageMessageInbox, m.id
			messageList.toggleSelect(1)
		then:
			waitFor('veryslow') { singleMessageDetails.sender == messageList.displayedNameFor(m) }
		when:
			messageList.toggleSelect(1)
		then:
			waitFor('veryslow') { messageList.messages[1].hasClass("selected") }
	}

	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createInboxTestMessages()
			Contact.build(name:'Alice', mobile:'Alice')
			Contact.build(name:'June', mobile:'+254778899')
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.replyAll.displayed }
		when:
			multipleMessageDetails.replyAll.click()
		then:
			waitFor { at QuickMessageDialog }
			waitFor { next.displayed }
	}

	def "the count of messages being sent is updated even in 'Reply all'"() {
		given:
			createInboxTestMessages()
			Contact.build(name:'Alice', mobile:'Alice')
			Contact.build(name:'June', mobile:'+254778899')
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.replyAll.displayed }
		when:
			multipleMessageDetails.replyAll.click()
		then:
			waitFor { at QuickMessageDialog }
			waitFor { next.displayed }
		when:
			next.click()
		then:
			confirm.messagesToSendCount == '2'
	}

	def "Should show the contact's name when replying to multiple messages from the same contact"() {
		given:
			Fmessage.build(src:'Alice', text:'hi Alice')
			Fmessage.build(src:'Alice', text:'test')
			Contact.build(name:'Alice', mobile:'Alice')
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.replyAll.displayed }
		when:
			multipleMessageDetails.replyAll.click()
		then:
			waitFor { at QuickMessageDialog }
			waitFor { next.displayed }
		when:
			next.click()
		then:
			waitFor { confirm.displayed }
			confirm.recipientName == "Alice"
	}

	def "'Forward' button works even when all messages are unchecked"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox, Fmessage.findBySrc('Alice').id
			messageList.selectAll.click()
		then:
			waitFor { messageList.selectedMessages.size() == 2 }
		when:
			messageList.selectAll.click()
		then:
			waitFor { singleMessageDetails.sender == "Alice" }
		when:
			singleMessageDetails.forward.click()
		then:
			waitFor { at QuickMessageDialog }
			waitFor { next.displayed }
		then:
			compose.textArea.text() == "hi Alice"
	}

	def "should uncheck message when a different message is clicked"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
		then:
			messageList.messages[0].checkbox.checked
		when:
			messageList.messages[1].textLink.click()
		then:
			waitFor('veryslow') { at PageMessageInbox }
			waitFor('verslow') { messageList.displayed }
 			!messageList.messages[0].checkbox.checked
	}

	def "select all should update the total message count when messages are checked"() {
		given:
			createInboxTestMessages()
			Fmessage.build()
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor('veryslow') { multipleMessageDetails.displayed }
			multipleMessageDetails.checkedMessageCount == '2 messages selected'
		when:
			messageList.toggleSelect(2)
		then:
			waitFor { multipleMessageDetails.checkedMessageCount == '3 messages selected' }
		when:
			messageList.toggleSelect(2)
		then:
			waitFor { multipleMessageDetails.checkedMessageCount == '2 messages selected' }
	}

	def "can archive multiple messages"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
		then:
			messageList.selectAll.click()
		then:
			waitFor { multipleMessageDetails.archiveAll.displayed }
		when:
			multipleMessageDetails.archiveAll.click()
		then:
			waitFor { at PageMessageInbox }
			singleMessageDetails.noneSelected

	}
}
