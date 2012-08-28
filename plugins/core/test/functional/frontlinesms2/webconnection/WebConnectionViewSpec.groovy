package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class WebConnectionViewSpec extends WebConnectionBaseSpec {
	def setup() {
		createTestWebConnections()
		createTestActivities()
		createTestMessages(WebConnection.findByName("Sync"))
	}

	@spock.lang.Unroll
	def "WebConnection page should show the details of the WebConnection in the header"() {
		setup:
			def webConnection  = WebConnection.findByName("Sync")
		when:
			to PageMessageWebConnection, webConnection
		then:
			waitFor { title?.toLowerCase().contains("command") }
			header[item] == value
		where:
			item		| value
			'name'		| "Sync WebConnection"
			'keyword'	| 'sync'
			'url'		| 'http://www.frontlinesms.com/sync'
			'sendMethod'| 'POST'
	}

	def "clicking the archive button archives the WebConnection and redirects to inbox "() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { header.displayed }
		when:
			header.archive.click()
		then:
			waitFor { at PageMessageInbox }
			notifications.flashMessageText == "Activity archived"
	}

	def "clicking the edit option opens the WebConnection Dialog for editing"() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("edit").jquery.click()
		then:
			waitFor("veryslow") { at WebConnectionWizard }
	}

	def "Clicking the Quick Message button brings up the Quick Message Dialog"() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
			waitFor { header.quickMessage.displayed }
			header.quickMessage.click()
		then:
			waitFor('veryslow'){ at QuickMessageDialog }
			waitFor{ compose.textArea.displayed }
	}

	def "clicking the rename option opens the rename small popup"() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("rename").jquery.click()
		then:
			waitFor { at RenameDialog }
			waitFor { webConnectionName.jquery.val().contains("Sync") }
	}

	def "clicking the delete option opens the confirm delete small popup"() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("delete").jquery.click()
		then:
			waitFor { at DeleteActivity }
	}

	def "clicking the export option opens the export dialog"() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("export").jquery.click()
		then:
			waitFor { at ExportDialog }
	}

	def "selecting a single message reveals the single message view"() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "Test message 0" }
	}

	def "selecting multiple messages reveals the multiple message view"() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
			waitFor { singleMessageDetails.displayed }
			messageList.messages[1].checkbox.click()		
		then:
			waitFor { multipleMessageDetails.displayed }
			waitFor { multipleMessageDetails.text?.toLowerCase() == "2 messages selected" }
	}

	def "clicking on a message reveals the single message view with clicked message"() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[3].checkbox.click()
		then:
			waitFor { singleMessageDetails.displayed }
			messageList.messages[3].hasClass("selected")
			singleMessageDetails.text == "Test message 3"
	}

	def "delete single message action works "() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.delete.click()
		then:
			waitFor { messageList.displayed }
			!messageList.messages*.text.contains("Test message 0")
	}

	def "delete multiple message action works for multiple select"(){
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
			waitFor {singleMessageDetails.displayed }
			messageList.messages[1].checkbox.click()
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.deleteAll.click()
		then:
			waitFor { messageList.displayed }
			!messageList.messages*.text.containsAll("Test message 0", "Test message 1")
	}

	def "move single message action works"() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "Test message 0" }
		when:
			singleMessageDetails.moveTo(Activity.findByName("Sample Announcement").id).click()
		then:
			waitFor("veryslow") { at PageMessageWebConnection }
			waitFor { notifications.flashMessageText.contains("updated") }
			!messageList.messages*.text.contains("Test message 0")
		when:
			to PageMessageAnnouncement, Activity.findByName("Sample Announcement")
		then:
			waitFor { messageList.displayed }
			messageList.messages*.text.contains("Test message 0")
	}

	def "move multiple message action works"() {
		when:
			to PageMessageWebConnection, WebConnection.findByName("Sync")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
			waitFor {singleMessageDetails.displayed }
			messageList.messages[1].checkbox.click()
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.moveTo(Activity.findByName("Sample Announcement").id).click()
		then:
			waitFor("veryslow") { notifications.flashMessageText.contains("updated") }
			!messageList.messages*.text.containsAll("Test message 0", "Test message 1")
		when:
			to PageMessageAnnouncement, Activity.findByName("Sample Announcement")
		then:
			waitFor { messageList.displayed }
			messageList.messages*.text.containsAll("Test message 0", "Test message 1")
	}
}