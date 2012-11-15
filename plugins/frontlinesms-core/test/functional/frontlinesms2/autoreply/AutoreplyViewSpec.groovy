package frontlinesms2.autoreply

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.announcement.PageMessageAnnouncement
import spock.lang.*

class AutoreplyViewSpec extends AutoreplyBaseSpec {

	def setup() {
		createTestAutoreply()
		createTestActivities()
		createTestMessages(Autoreply.findByName("Fruits"))
	}

	@Unroll
	def "autoreply page should show the details of the autoreply in the header"() {		
		setup:
			def autoreply  = Autoreply.findByName("Fruits")
		when:						
			to PageMessageAutoreply, autoreply
		then:
			waitFor { header.title?.toLowerCase().contains("autoreply") }
			
			header[item] == value
		where:
			item				| value
			'title'				| "fruits autoreply"			
			'autoreplyMessage'  | 'Hello, this is an autoreply message'			
	}
	
	def "clicking the edit option opens the Autoreply Dialog for editing"() {
		when:
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("edit").jquery.click()
		then:
			waitFor("veryslow") { at AutoreplyCreateDialog }
	}

	def "Clicking the Quick Message button brings up the Quick Message Dialog"() {
		when:
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
			waitFor { header.quickMessage.displayed }
			header.quickMessage.click()
		then:
			waitFor('veryslow'){ at QuickMessageDialog }
			waitFor{ compose.textArea.displayed }
	}

	def "clicking the rename option opens the rename small popup"() {
		when:
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("rename").jquery.click()
		then:
			waitFor { at RenameActivityDialog }
			waitFor { activityName.jquery.val().contains("Fruits") }
	}

	def "clicking the delete option opens the confirm delete small popup"() {
		when:
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("delete").jquery.click()
		then:
			waitFor { at DeleteActivity }
	}

	def "clicking the export option opens the export dialog"() {
		when:
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("export").jquery.click()
		then:
			waitFor { at ExportDialog }
	}

	def "selecting a single message reveals the single message view"() {
		when:
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
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
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
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
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
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
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
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
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
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
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
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
			waitFor("veryslow") { at PageMessageAutoreply }
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
			to PageMessageAutoreply, Autoreply.findByName("Fruits")
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

	def "moving a message from another activity to a autoreply displays an update message"() {
		setup:
			def activity = Activity.findByName("Sample Announcement")
			def m = Fmessage.findBySrc("announce")
			def autoreply = Autoreply.findByName('Fruits')
		when:
			to PageMessageAnnouncement, activity.id, m.id
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.moveTo(autoreply.id)
		then:		
			waitFor { flashMessage.displayed }
	}

	def "clicking on the sent message filter should display outgoing messages only"() {
		given:
			def a = createInAndOutTestMessages()
		when:
			to PageMessageAutoreply, a
		then:
			messageList.messages.size() == 5
		when:
			footer.showOutgoing.click()
		then:
			waitFor { messageList.messages.size() == 2 }
	}
	
	def "clicking on the received message filter should display incoming messages only"() {
		given:
			def a = createInAndOutTestMessages()
		when:
			to PageMessageAutoreply, a
		then:
			messageList.messages.size() == 5
		when:
			footer.showIncoming.click()
		then:
			waitFor { messageList.messages.size() == 3 }
	}

	private Autoreply createInAndOutTestMessages() {
		Autoreply a = Autoreply.build(name:'Vegetables')
		3.times { a.addToMessages(Fmessage.build()) }
		2.times {
			def sentMessage = Fmessage.buildWithoutSave(inbound:false)
			sentMessage.addToDispatches(dst:'123456789', status:DispatchStatus.PENDING)
			sentMessage.save(failOnError:true, flush:true)
			a.addToMessages(sentMessage) }
		a.save(failOnError:true, flush:true)
		return a
	}
}

