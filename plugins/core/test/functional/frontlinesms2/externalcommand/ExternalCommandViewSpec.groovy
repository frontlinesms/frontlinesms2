package frontlinesms2.externalcommand

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class ExternalCommandViewSpec extends ExternalCommandBaseSpec {
	def setup() {
		createTestExternalCommands()
		createTestActivities()
		createTestMessages(ExternalCommand.findByName("Sync"))
	}

	@spock.lang.Unroll
	def "ExternalCommand page should show the details of the ExternalCommand in the header"() {
		setup:
			def externalCommand  = ExternalCommand.findByName("Sync")
		when:
			to PageMessageExternalCommand, externalCommand
		then:
			waitFor { title?.toLowerCase().contains("command") }
			header[item] == value
		where:
			item		| value
			'name'		| "Sync ExternalCommand"
			'keyword'	| 'sync'
			'url'		| 'http://www.frontlinesms.com/sync'
			'sendMethod'| 'POST'
	}

	def "clicking the archive button archives the ExternalCommand and redirects to inbox "() {
		when:
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
		then:
			waitFor { header.displayed }
		when:
			header.archive.click()
		then:
			waitFor { at PageMessageInbox }
			notifications.flashMessageText == "Activity archived"
	}

	def "clicking the edit option opens the ExternalCommand Dialog for editing"() {
		when:
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("edit").jquery.click()
		then:
			waitFor("veryslow") { at ExternalCommandWizard }
	}

	def "Clicking the Quick Message button brings up the Quick Message Dialog"() {
		when:
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
			waitFor { header.quickMessage.displayed }
			header.quickMessage.click()
		then:
			waitFor('veryslow'){ at QuickMessageDialog }
			waitFor{ compose.textArea.displayed }
	}

	def "clicking the rename option opens the rename small popup"() {
		when:
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("rename").jquery.click()
		then:
			waitFor { at RenameDialog }
			waitFor { externalCommandName.jquery.val().contains("Sync") }
	}

	def "clicking the delete option opens the confirm delete small popup"() {
		when:
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("delete").jquery.click()
		then:
			waitFor { at DeleteActivity }
	}

	def "clicking the export option opens the export dialog"() {
		when:
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("export").jquery.click()
		then:
			waitFor { at ExportDialog }
	}

	def "selecting a single message reveals the single message view"() {
		when:
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
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
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
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
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
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
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
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
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
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
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
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
			waitFor("veryslow") { at PageMessageExternalCommand }
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
			to PageMessageExternalCommand, ExternalCommand.findByName("Sync")
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