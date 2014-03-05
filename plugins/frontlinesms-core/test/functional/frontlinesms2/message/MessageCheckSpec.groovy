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
			waitFor('veryslow') { multipleMessageDetails.checkedMessageCount == 2 }
	}

	def "checked message details are displayed when message is checked"() {
		given:
			createInboxTestMessages()
			def m = remote { TextMessage.findBySrc('Bob').id }
		when:
			to PageMessageInbox, m
			messageList.toggleSelect(1)
		then:
			waitFor('veryslow') { singleMessageDetails.sender == messageList.displayedNameFor(m) }
		when:
			messageList.toggleSelect(1)
		then:
			waitFor('veryslow') { messageList.hasClass(1, "selected") }
	}

	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createInboxTestMessages()
			remote {
				Contact.build(name:'Alice', mobile:'Alice')
				Contact.build(name:'June', mobile:'+254778899')
				null
			}
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
	}

	def "the count of messages being sent is updated even in 'Reply all'"() {
		given:
			createInboxTestMessages()
			remote {
				Contact.build(name:'Alice', mobile:'Alice')
				Contact.build(name:'June', mobile:'+254778899')
				null
			}
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
			messagesToSendCount() == '2'
	}

	def "Should show the contact's name when replying to multiple messages from the same contact"() {
		given:
			remote {
				TextMessage.build(src:'Alice', text:'hi Alice')
				TextMessage.build(src:'Alice', text:'test')
				Contact.build(name:'Alice', mobile:'Alice')
				null
			}
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
			recipientName() == "Alice"
	}

	def "'Forward' button works even when all messages are unchecked"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox, remote { TextMessage.findBySrc('Alice').id }
			messageList.selectAll.click()
		then:
			waitFor { messageList.selectedMessageCount() == 2 }
		when:
			messageList.selectAll.click()
		then:
			waitFor { singleMessageDetails.sender == "Alice" }
		when:
			singleMessageDetails.forward.click()
		then:
			waitFor { at QuickMessageDialog }
			textArea.text() == "hi Alice"
	}

	def "should uncheck message when a different message is clicked"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
		then:
			messageList.isChecked(0)
		when:
			messageList.clickLink(1)
		then:
			waitFor('veryslow') { at PageMessageInbox }
			waitFor('verslow') { messageList.displayed }
			!messageList.isChecked(0)
	}

	def "select all should update the total message count when messages are checked"() {
		given:
			createInboxTestMessages()
			remote { TextMessage.build(); null }
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor('veryslow') { multipleMessageDetails.displayed }
			multipleMessageDetails.checkedMessageCount == 2
		when:
			messageList.toggleSelect(2)
		then:
			waitFor { multipleMessageDetails.checkedMessageCount == 3 }
		when:
			messageList.toggleSelect(2)
		then:
			waitFor { multipleMessageDetails.checkedMessageCount == 2 }
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

