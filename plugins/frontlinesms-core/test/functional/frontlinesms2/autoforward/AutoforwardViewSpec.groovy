package frontlinesms2.autoforward

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.announcement.PageMessageAnnouncement
import spock.lang.*

class AutoforwardViewSpec extends AutoforwardBaseSpec {

	def setup() {
		createTestAutoforward()
		createTestActivities()
		createTestMessages(Autoforward.findByName("News"))
	}

	@Unroll
	def "autoforward page should show the details of the autoforward in the header"() {
		setup:
			def autoforward  = Autoforward.findByName("News")
		when:
			to PageMessageAutoforward, autoforward
		then:
			waitFor { header.title?.toLowerCase().contains("autoforward") }
			header[item] == value
		where:
			item         | value
			'title'      | "news autoforward"
			'message'    | 'Message: Message is Message text'
			'keywords'   | 'Keywords : BREAKING,ALERT'
			'recipients' | 'Currently 10 recipients'
	}

	def "clicking the edit option opens the Autoforward Dialog for editing"() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("edit").jquery.click()
		then:
			waitFor("veryslow") { at AutoforwardCreateDialog }
	}

	def "Clicking the Quick Message button brings up the Quick Message Dialog"() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
			waitFor { header.quickMessage.displayed }
			header.quickMessage.click()
		then:
			waitFor('veryslow'){ at QuickMessageDialog }
			waitFor{ compose.textArea.displayed }
	}

	def "clicking the rename option opens the rename small popup"() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("rename").jquery.click()
		then:
			waitFor { at RenameActivityDialog }
			waitFor { activityName.jquery.val().contains("News") }
	}

	def "clicking the delete option opens the confirm delete small popup"() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("delete").jquery.click()
		then:
			waitFor { at DeleteActivity }
	}

	def "clicking the export option opens the export dialog"() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("export").jquery.click()
		then:
			waitFor { at ExportDialog }
	}

	def "selecting a single message reveals the single message view"() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == 'Sudden shock 0' }
	}

	def "selecting multiple messages reveals the multiple message view"() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor { singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
			waitFor { multipleMessageDetails.text?.toLowerCase() == "2 messages selected" }
	}

	def "clicking on a message reveals the single message view with clicked message"() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(3)
		then:
			waitFor { singleMessageDetails.displayed }
			messageList.messages[3].hasClass("selected")
			singleMessageDetails.text == "Sudden shock 3"
	}

	def "delete single message action works "() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.delete.click()
		then:
			waitFor { messageList.displayed }
			!messageList.messages*.text.contains("Sudden shock 0")
	}

	def "delete multiple message action works for multiple select"(){
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor {singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.deleteAll.click()
		then:
			waitFor { messageList.displayed }
			!messageList.messages*.text.containsAll("Sudden shock 0", "Sudden shock 1")
	}

	def "move single message action works"() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "Sudden shock 0" }
		when:
			singleMessageDetails.moveTo(Activity.findByName("Sample Announcement").id).click()
		then:
			waitFor("veryslow") { at PageMessageAutoforward }
			waitFor { notifications.flashMessageText.contains("updated") }
			!messageList.messages*.text.contains("Sudden shock 0")
		when:
			to PageMessageAnnouncement, Activity.findByName("Sample Announcement")
		then:
			waitFor { messageList.displayed }
			messageList.messages*.text.contains("Sudden shock 0")
	}

	def "move multiple message action works"() {
		when:
			to PageMessageAutoforward, Autoforward.findByName("News")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor {singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.moveTo(Activity.findByName("Sample Announcement").id).click()
		then:
			waitFor("veryslow") { notifications.flashMessageText.contains("updated") }
			!messageList.messages*.text.containsAll("Sudden shock 0", "Sudden shock 1")
		when:
			to PageMessageAnnouncement, Activity.findByName("Sample Announcement")
		then:
			waitFor { messageList.displayed }
			messageList.messages*.text.containsAll("Sudden shock 0", "Sudden shock 1")
	}

	def "moving a message from another activity to a autoforward displays an update message"() {
		setup:
			def activity = Activity.findByName("Sample Announcement")
			def m = Fmessage.findBySrc("announce")
			def autoforward = Autoforward.findByName('News')
		when:
			to PageMessageAnnouncement, activity.id, m.id
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.moveTo(autoforward.id)
		then:		
			waitFor { flashMessage.displayed }
	}

	def "clicking on the sent message filter should display outgoing messages only"() {
		given:
			def a = createInAndOutTestMessages()
		when:
			to PageMessageAutoforward, a
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
			to PageMessageAutoforward, a
		then:
			messageList.messages.size() == 5
		when:
			footer.showIncoming.click()
		then:
			waitFor { messageList.messages.size() == 3 }
	}

	private Autoforward createInAndOutTestMessages() {
		def a = new Autoforward(name:"Vegetables")
		a.addToContacts(new Contact(name:"name"))
		a.addToKeywords(value:"VEGS")
		a.sentMessageText = 'Message is \${message_text}'
		3.times { a.addToMessages(Fmessage.build()) }
		2.times {
			def sentMessage = new Fmessage(text:'this is a sent message',inbound:false)
			sentMessage.addToDispatches(dst:'123456789', status:DispatchStatus.PENDING)
			a.addToMessages(sentMessage) }
		a.save(flush:true, failOnError:true)
	}
}
