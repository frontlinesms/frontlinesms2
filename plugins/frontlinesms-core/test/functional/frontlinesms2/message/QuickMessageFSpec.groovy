package frontlinesms2.message

import frontlinesms2.*
import java.util.regex.*
import frontlinesms2.popup.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class QuickMessageFSpec extends grails.plugin.geb.GebSpec {
	def "quick message link opens the popup to send messages"() {
		when:
			launchQuickMessageDialog()
		then:
			at QuickMessageDialog
	}

	def "should add the manually entered contacts to the list "() {
		when:
			launchQuickMessageDialog()
			waitFor { textArea.displayed }
			recipients.addRecipient("+919544426000")
		then:
			waitFor { recipients.count == 1 }
	}
	
	def "should not add the same manually entered contact more than once "() {
		when:
			launchQuickMessageDialog()
			waitFor { textArea.displayed }
			recipients.addRecipient("+919544426000")
			recipients.addRecipient("+919544426000")
		then:
			waitFor { recipients.count == 1 }
		when:
			recipients.addRecipient("+221122")
		then:
			waitFor { recipients.count == 2 }
	}

	def "dropping a manually added contact updates the recipients count "() {
		when:
			launchQuickMessageDialog()
			waitFor { textArea.displayed }
			recipients.addRecipient("+919544426000")
		then:
			waitFor { recipients.count == 1 }
		when:
			recipients.removeRecipient("+919544426000")
		then:
			waitFor { recipients.count == 0 }
	}

	def "should send the message to the selected recipients"() {
		when:
			launchQuickMessageDialog()
			waitFor { textArea.displayed }
			textArea << "hi there"
			textArea.jquery.trigger('keyup')
			recipients.addRecipient("+919544426000")
			submit.click()
		then:
			at PageMessageInbox
			waitFor { notifications.flashMessage.displayed }
		when:
			to PageMessagePending
		then:
			waitFor { messageList.messageCount() == 1 }
	}

	def "should select members belonging to the selected group"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			waitFor { recipients.displayed }
			recipients.addRecipient('group1')
		then:
			waitFor { messagesToSendCount() == '2' }
	}

	def "should not allow to proceed if the recipients are not selected in the quick message screen"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			recipients.displayed
			textArea << "hi there"
			textArea.jquery.trigger('keyup')
			submit.click()
		then:
			waitFor { errorPanel.displayed }
	}

	def "should show the character count of each message"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
		then:
			waitFor { charCount() == '0' }
		when:
			textArea << "h"
			textArea.jquery.trigger('keyup')
		then:
			waitFor { charCount() == '1' }
	}
	
	def "magic wand should be available"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
		then:
			waitFor { magicWand.displayed }
	}

	def "using contact name magic wand option should insert substitution variable"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
		then:
			waitFor { charCount() == '0' }
		when:
			textArea << "Hello, "
			textArea.jquery.trigger('keyup')
		then:
			magicWand.jquery.val('recipient_number')
			magicWand.jquery.trigger('change')
		then:
			waitFor { textArea.jquery.val() == 'Hello, ${recipient_number}' }
	}
	
	private def createData() {
		remote {
			def group = Group.build(name:"group1")
			def group2 = Group.build(name:"group2")
			def alice = Contact.build(name:"alice", mobile:"12345678")
			def bob = Contact.build(name:"bob", mobile:"567812445")
			group.addToMembers(alice)
			group2.addToMembers(alice)
			group.addToMembers(bob)
			group2.addToMembers(bob)
			group.save(flush:true)
			group2.save(flush:true)
			null
		}
	}
	
	def launchQuickMessageDialog() {
		to PageMessageInbox
		header.quickMessage.click()
		waitFor { at QuickMessageDialog }
	}
	
}
