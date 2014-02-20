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

		createTestMessages("News")
	}

	@Unroll
	def "autoforward page should show the details of the autoforward in the header"() {
		when:
			to PageMessageAutoforward, 'News'
		then:
			waitFor { header.title?.toLowerCase().contains("autoforward") }
			header[item] == value
		where:
			item         | value
			'title'      | "autoforward.title[news]"
			'message'    | 'autoforward.message.format: Message is dynamicfield.message_text.label'
			'keywords'   | 'poll.keywords : BREAKING,ALERT'
			'recipients' | 'autoforward.recipientcount.current[10]'
	}

	def "clicking the edit option opens the Autoforward Dialog for editing"() {
		when:
			to PageMessageAutoforward, 'News'
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("edit").jquery.click()
		then:
			waitFor("veryslow") { at AutoforwardCreateDialog }
	}

	def "Clicking the Quick Message button brings up the Quick Message Dialog"() {
		when:
			to PageMessageAutoforward, 'News'
			waitFor { header.quickMessage.displayed }
			header.quickMessage.click()
		then:
			waitFor('veryslow'){ at QuickMessageDialog }
			waitFor{ textArea.displayed }
	}

	def "clicking the rename option opens the rename small popup"() {
		when:
			to PageMessageAutoforward, 'News'
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
			to PageMessageAutoforward, 'News'
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("delete").jquery.click()
		then:
			waitFor { at DeleteActivity }
	}

	def "clicking the export option opens the export dialog"() {
		when:
			to PageMessageAutoforward, 'News'
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("export").jquery.click()
		then:
			waitFor { at ExportDialog }
	}

	def "selecting a single message reveals the single message view"() {
		when:
			to PageMessageAutoforward, 'News'
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
			to PageMessageAutoforward, 'News'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor { singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
			waitFor { multipleMessageDetails.text?.toLowerCase() == "message.multiple.selected[2]" }
	}

	def "clicking on a message reveals the single message view with clicked message"() {
		when:
			to PageMessageAutoforward, 'News'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(3)
		then:
			waitFor { singleMessageDetails.displayed }
			messageList.hasClass(3, "selected")
			singleMessageDetails.text == "Sudden shock 3"
	}

	def "delete single message action works "() {
		when:
			to PageMessageAutoforward, 'News'
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
			messageList.messageText(0) != 'Sudden shock 0'
	}

	def "delete multiple message action works for multiple select"(){
		when:
			to PageMessageAutoforward, 'News'
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
			!(messageList.messageText(0) in ['Sudden shock 0', 'Sudden shock 1'])
			!(messageList.messageText(1) in ['Sudden shock 0', 'Sudden shock 1'])
	}

	def "move single message action works"() {
		when:
			to PageMessageAutoforward, 'News'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "Sudden shock 0" }
		when:
			singleMessageDetails.moveTo(remote { Activity.findByName("Sample Announcement").id }).click()
		then:
			waitFor("veryslow") { at PageMessageAutoforward }
			waitFor { notifications.flashMessageText.contains("updated") }
			messageList.messageText(0) != 'Sudden shock 0'
		when:
			to PageMessageAnnouncement, 'Sample Announcement'
		then:
			waitFor { messageList.displayed }
			messageList.messageText(0) == 'Sudden shock 0'
	}

	def "move multiple message action works"() {
		when:
			to PageMessageAutoforward, 'News'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor { singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.moveTo(remote { Activity.findByName("Sample Announcement").id }).click()
		then:
			waitFor("veryslow") { notifications.flashMessageText.contains("updated") }
			!(messageList.messageText(0) in ['Sudden shock 0', 'Sudden shock 1'])
			!(messageList.messageText(1) in ['Sudden shock 0', 'Sudden shock 1'])
		when:
			to PageMessageAnnouncement, 'Sample Announcement'
		then:
			waitFor { messageList.displayed }
			messageList.messageText(0) in ['Sudden shock 0', 'Sudden shock 1']
			messageList.messageText(1) in ['Sudden shock 0', 'Sudden shock 1']
	}

	def "moving a message from another activity to a autoforward displays an update message"() {
		setup:
			def activity = remote { Activity.findByName("Sample Announcement").id }
			def m = remote { TextMessage.findBySrc("announce").id }
			def autoforward = remote { Autoforward.findByName('News').id }
		when:
			to PageMessageAnnouncement, activity, m
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.moveTo(autoforward)
		then:
			waitFor { flashMessage.displayed }
	}

	def "clicking on the sent message filter should display outgoing messages only"() {
		given:
			def a = createInAndOutTestMessages()
		when:
			to PageMessageAutoforward, a
		then:
			messageList.messageCount() == 5
		when:
			footer.showOutgoing.click()
		then:
			waitFor { messageList.messageCount() == 2 }
	}

	def "clicking on the received message filter should display incoming messages only"() {
		given:
			def a = createInAndOutTestMessages()
		when:
			to PageMessageAutoforward, a
		then:
			messageList.messageCount() == 5
		when:
			footer.showIncoming.click()
		then:
			waitFor { messageList.messageCount() == 3 }
	}

	private createInAndOutTestMessages() {
		remote {
			def a = new Autoforward(name:"Vegetables")
			a.addToContacts(new Contact(name:"name"))
			a.addToKeywords(value:"VEGS")
			a.sentMessageText = 'Message is \${message_text}'
			3.times { a.addToMessages(TextMessage.build()) }
			2.times {
				def sentMessage = new TextMessage(text:'this is a sent message',inbound:false)
				sentMessage.addToDispatches(dst:'123456789', status:DispatchStatus.PENDING)
				a.addToMessages(sentMessage) }
			a.save(flush:true, failOnError:true)
			return a.id
		}
	}
}
