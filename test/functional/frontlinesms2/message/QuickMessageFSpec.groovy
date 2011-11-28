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
		then:
			$("#recipient-count").text() == "1"
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
			$("a", text: "Pending").hasClass("send-failed")
		when:
			$("a", href: "/frontlinesms2/message/pending").click()
		then:
			waitFor{ title == "Pending" }
			$("#message-list tbody tr").size() == 1
			$("#message-list tbody tr")[0].hasClass("send-failed")
	}

	def "should select members belonging to the selected group"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
		then:
			$("input[name=groups]").displayed
		when:
			$("input[name=groups]").value("group1")
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
			$("input[name=groups]").displayed
		when:
			$("input[name=groups]").value("group1")
		then:
			waitFor { $("#recipient-count").text() == "2" }
		when:
			$("input[value=group1]").click()
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
			$("input[value='group1']").click()
			$("input[value='group2']").click()
		then:
			$("#recipient-count").text() == "2"
		when:
			$("input[value='12345678']").click()
		then:
			!$("input[value='group1']").checked
			!$("input[value='group2']").checked
			$("#recipient-count").text() == "1"
	}

	def "should not deselect common members across groups when one of the group is unchecked"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
			$("input[value='group1']").click()
			$("input[value='group2']").click()
		then:
			$("#recipient-count").text() == "2"
		when:
			$("input[value='group1']").click()
		then:
			!$("input[value='group1']").checked
			$("input[value='group2']").checked
			$("#recipient-count").text() == "2"

	}

	def "should show the character count of each message"() {
		setup:
			createData()
		when:
			launchQuickMessageDialog()
		then:
			waitFor { characterCount.text() == "Characters remaining 160 (1 SMS message)" }
		when:
			$("#messageText").value("h")
		then:
			waitFor { characterCount.text() == "Characters remaining 159 (1 SMS message)" }
	}
	
	@spock.lang.IgnoreRest
	def "should not deselect group when a non-member contact is unchecked"() {
		setup:
			createData()
			def contact = new Contact(name:"Test", primaryMobile:"876543212").save(flush:true)
		when:
			launchQuickMessageDialog()
			toSelectRecipientsTab()
			$("input[value='group1']").click()
		then:
			$("input[value='group1']").checked
		when:
			$("input[value='876543212']").click()
		then:
			$("input[value='group1']").checked
		when:
			$("input[value='876543212']").click()
		then:
			$("input[value='group1']").checked

	}
	
	private def createData() {
		def group = new Group(name: "group1").save(flush: true)
		def group2 = new Group(name: "group2").save(flush: true)
		def alice = new Contact(name: "alice", primaryMobile: "12345678").save(flush: true)
		def bob = new Contact(name: "bob", primaryMobile: "567812445").save(flush: true)
		group.addToMembers(alice)
		group2.addToMembers(alice)
		group.addToMembers(bob)
		group2.addToMembers(bob)
		group.save(flush: true)
		group2.save(flush: true)
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
		$("#ui-dialog-title-modalBox").text() == 'Quick Message'
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
	}
}

class SentMessagesPage extends geb.Page {
	static url = 'message/sent'
}
