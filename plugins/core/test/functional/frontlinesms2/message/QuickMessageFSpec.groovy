package frontlinesms2.message

import frontlinesms2.*
import java.util.regex.*
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
			toSelectRecipientsTab()
			addressField.value("+919544426000")
			addAddressButton.click()
		then:
			waitFor { selectRecipientsTab.displayed }
		when:
			toConfirmTab()
		then:
			waitFor { confirmTab.displayed }
	}

	def "should select the previous tab on click of back"() {
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
			addressField.value("+919544426000")
			addAddressButton.click()
			toConfirmTab()
		then:
			waitFor { confirmTab.displayed }
		when:
			$("#prevPage").click()
		then:
			waitFor { selectRecipientsTab.displayed }

	}

	def "should add the manually entered contacts to the list "() {
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
			addressField.value("+919544426000")
			addAddressButton.click()
		then:
			waitFor { $('#contacts').find('input', type:'checkbox').value() == "+919544426000" }
			$("#recipient-count").text() == "1"
	}
	
	def "should not add the same manually entered contact more than once "() {
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
			addressField.value("+919544426000")
			addAddressButton.click()
			addressField.value("+919544426000")
			addAddressButton.click()
		then:
			waitFor { $('#contacts').find('input', type:'checkbox').value() == "+919544426000" }
			$("#recipient-count").text() == "1"
		when:
			addressField.value("3232")
			addAddressButton.click()
		then:
			waitFor { $('#contacts').find('input', type:'checkbox').value() == "3232" }
			$("#recipient-count").text() == "2"
	}

	def "unchecking a manually added contact updates the recipients count "() {
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
			addressField.value("+919544426000")
			addAddressButton.click()
			addressField.value("+919544426009")
			addAddressButton.click()
		then:
			waitFor { $('#contacts').find('input', type:'checkbox').value() == "+919544426009" }
			$("#recipient-count").text() == "2"
		when:
			$("li.manual").find("input", name:"addresses")[0].click()
			$("nextPage").click()
		then:
			waitFor { $("#recipient-count").text() == "1"	 }
	}
	
	def "should send the message to the selected recipients"() {
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
			addressField.value("+919544426000")
			addAddressButton.click()
		then:
			waitFor { $('#contacts').find('input', type:'checkbox').value() == "+919544426000" }
		when:
			toConfirmTab()
			doneButton.click()
		then:
			waitFor { $(".flash").displayed }
		when:
			$("a", text:contains("Pending")).click()
		then:
			waitFor { $("a", text:contains("Pending")).hasClass("pending-send-failed") }
		then:
			waitFor { $('h3.pending').text().equalsIgnoreCase("Pending") }
			$("a", text:contains("Pending")).hasClass("pending-send-failed")
			$("#message-list tr").size() == 2
	}

	def "should select members belonging to the selected group"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
		then:
			groupCheckbox[0].displayed
		when:
			groupCheckbox[0].click()
		then:
			waitFor { $("#recipient-count").text() == "2" }
	}

	def "should deselect all member recipients when a group is un checked"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
		then:
			groupCheckbox[0].displayed
		when:
			groupCheckbox[0].click()
		then:
			waitFor { $("#recipient-count").text() == "2" }
		when:
			groupCheckbox[0].click()
		then:
			waitFor { $("#recipient-count").text() == "0" }
	}

	def "should not allow to proceed if the recipients are not selected in the quick message screen"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
		then:
			!$(".error-panel").displayed
		when:
			nextPageButton.click()
		then:
			waitFor { $(".error-panel").displayed }
	}

	def "selected group should get unchecked when a member drops off"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
			groupCheckbox[0].click()
			groupCheckbox[1].click()
		then:
			$("#recipient-count").text() == "2"
		when:
			$("input[value='12345678']").click()
		then:
			!groupCheckbox[0].checked
			!groupCheckbox[1].checked
			$("#recipient-count").text() == "1"
	}

	def "should not deselect common members across groups when one of the group is unchecked"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
			groupCheckbox[0].click()
			groupCheckbox[1].click()
		then:
			$("#recipient-count").text() == "2"
		when:
			groupCheckbox[0].click()
		then:
			!groupCheckbox[0].checked
			groupCheckbox[1].checked
			$("#recipient-count").text() == "2"

	}

	def "should show the character count of each message"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
		then:
			waitFor { characterCount.text() == "Characters remaining 160 (1 SMS message(s))" }
		when:
			$("#messageText") << "h"
			$("#messageText").jquery.trigger('keyup')
		then:
			waitFor { characterCount.text() == "Characters remaining 159 (1 SMS message(s))" }
	}
	
	def "should not deselect group when a non-member contact is unchecked"() {
		setup:
			createData()
			def contact = Contact.build(name:"Test", mobile:"876543212")
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
			groupCheckbox[0].click()
		then:
			groupCheckbox[0].checked
		when:
			$("input[value='876543212']").click()
		then:
			groupCheckbox[0].checked
		when:
			$("input[value='876543212']").click()
		then:
			groupCheckbox[0].checked

	}

	def "magic wand should be available and clicking should display menu"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
		then:
			waitFor { $("#magicwand-selectmessageText").displayed }
	}

	def "using contact name magic wand option should insert substitution variable"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
		then:
			waitFor { characterCount.text() == "Characters remaining 160 (1 SMS message(s))" }
		when:
			$("#messageText") << "Hello, "
			$("#messageText").jquery.trigger('keyup')
		then:
			$("#magicwand-selectmessageText").jquery.val('contact_name')
			$("#magicwand-selectmessageText").jquery.trigger('change')
		then:
			waitFor { $('#messageText').jquery.val() == 'Hello, ${contact_name}' }
	}
	
	private def createData() {
		def group = Group.build(name:"group1")
		def group2 = Group.build(name:"group2")
		def alice = Contact.build(name:"alice", mobile:"12345678")
		def bob = Contact.build(name:"bob", mobile:"567812445")
		group.addToMembers(alice)
		group2.addToMembers(alice)
		group.addToMembers(bob)
		group2.addToMembers(bob)
	}
	
	def launchQuickMessageDialog() {
		to PageMessageInbox
		$("a", text:"Quick message").click()
		waitFor { at QuickMessageDialog }
	}
	
	def toSelectRecipientsTab() {
		$('a', text:'Select recipients').click()
		waitFor { selectRecipientsTab.displayed }
	}
	
	def toConfirmTab() {
		$('a', text:'Confirm').click()
		waitFor { confirmTab.displayed }
	}
}

class QuickMessageDialog extends geb.Page {
	static at = {
		$("#ui-dialog-title-modalBox").text().equalsIgnoreCase('Quick Message')
	}
	static content = {
		selectRecipientsTab { $('div#tabs-2') }
		confirmTab { $('div#tabs-3') }
		messagesQueuedNotification { $(".summary") }
		
		addressField { $('#address') }
		addAddressButton { $('.add-address') }
		
		doneButton { $("#submit") }
		nextPageButton { $("#nextPage") }
		characterCount { $("#send-message-stats")}
		messagesCount { $("#messages-count")}

		groupCheckbox { $('input', type:'checkbox', name:'groups') }
	}
}

class SentMessagesPage extends geb.Page {
	static url = 'message/sent'
}
