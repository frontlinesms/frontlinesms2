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

	def "should select the next tab on click of next"() {
		when:
			launchQuickMessageDialog()
		then:
			waitFor { compose.displayed }
		when:
			next.click()
		then:
			waitFor { recipients.displayed }
	}

	def "should select the previous tab on click of back"() {
		when:
			launchQuickMessageDialog()
		then:
			waitFor { compose.displayed }
		when:
			next.click()
		then:
			waitFor { recipients.displayed }
		when:
			previous.click()
		then:
			waitFor { compose.displayed }

	}

	def "should add the manually entered contacts to the list "() {
		when:
			launchQuickMessageDialog()
			waitFor { compose.displayed }
			next.click()
			recipients.addRecipient("+919544426000")
		then:
			waitFor { recipients.count == 1 }
	}
	
	def "should not add the same manually entered contact more than once "() {
		when:
			launchQuickMessageDialog()
			waitFor { compose.displayed }
			next.click()
			recipients.addRecipient("+919544426000")
			recipients.addRecipient("+919544426000")
		then:
			waitFor { recipients.count == 1 }
		when:
			recipients.addRecipient("+221122")
		then:
			waitFor { recipients.count == 2 }
	}

	def "unchecking a manually added contact updates the recipients count "() {
		when:
			launchQuickMessageDialog()
			waitFor { compose.displayed }
			next.click()
			recipients.addRecipient("+919544426000")
			recipients.addRecipient("+919544426000")
		then:
			waitFor { recipients.count == 1 }
		when:
			recipients.manualContacts[0].click()
		then:
			waitFor { recipients.count == 0 }
	}

	def "should send the message to the selected recipients"() {
		when:
			launchQuickMessageDialog()
			waitFor { compose.displayed }
			next.click()
			recipients.addRecipient("+919544426000")
			next.click()
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
			waitFor { next.displayed }
			next.click()
		then:
			recipients.groupCheckboxes[0].displayed
		when:
			recipients.groupCheckboxes[0].click()
		then:
			waitFor { recipients.count == 2 }
	}

	def "should not allow to proceed if the recipients are not selected in the quick message screen"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			waitFor { next.displayed }
			next.click()
		then:
			recipients.groupCheckboxes[0].displayed
		when:
			next.click()
		then:
			recipients.displayed
			waitFor { errorPanel.displayed }
	}

	def "when common memeber is deselected the count should not change unless all parent groups are deselected"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			waitFor { next.displayed }
			next.click()
			recipients.groupCheckboxes[0].click()
			recipients.groupCheckboxes[1].click()
		then:
			waitFor { recipients.count == 2 }
		when:
			recipients.groupCheckboxes[0].click()
		then:
			!recipients.groupCheckboxes[0].checked
			recipients.groupCheckboxes[1].checked
			waitFor { recipients.count == 2 }

	}

	def "should show the character count of each message"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
		then:
			waitFor { compose.wordCount == 'message.character.count[160,1]' }
		when:
			compose.textArea << "h"
			compose.textArea.jquery.trigger('keyup')
		then:
			waitFor { compose.wordCount == 'message.character.count[159,1]' }
	}
	
	def "should not deselect group when a non-member contact is unchecked"() {
		setup:
			createData()
			remote { Contact.build(name:"Test", mobile:"876543212"); null }
		when:
			launchQuickMessageDialog()
			waitFor { compose.displayed }
			next.click()
			recipients.groupCheckboxes[0].click()
		then:
			recipients.groupCheckboxes[0].checked
		when:
			recipients.recipientCheckboxByValue("876543212").click()
		then:
			recipients.groupCheckboxes[0].checked
		when:
			recipients.recipientCheckboxByValue("876543212").click()
		then:
			recipients.groupCheckboxes[0].checked

	}

	def "magic wand should be available and clicking should display menu"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
		then:
			waitFor { compose.magicWand.displayed }
	}

	def "using contact name magic wand option should insert substitution variable"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
		then:
			waitFor { compose.wordCount == 'message.character.count[160,1]' }
		when:
			compose.textArea << "Hello, "
			compose.textArea.jquery.trigger('keyup')
		then:
			compose.magicWand.jquery.val('recipient_number')
			compose.magicWand.jquery.trigger('change')
		then:
			waitFor { compose.textArea.jquery.val() == 'Hello, ${recipient_number}' }
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
	
	def toRecipientsTab() {
		at QuickMessageDialog
		tab(2).click()
		waitFor { recipients.displayed }
	}
	
	def toConfirmTab() {
		at QuickMessageDialog
		tab(3).click()
		waitFor { confirm.displayed }
	}
}
