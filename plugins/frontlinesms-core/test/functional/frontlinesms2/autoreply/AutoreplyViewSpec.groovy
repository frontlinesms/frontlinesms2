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
		createTestMessages('Fruits')
	}

	@Unroll
	def "autoreply page should show the details of the autoreply in the header"() {
		when:
			to PageMessageAutoreply, 'Fruits'
		then:
			waitFor { header.title?.toLowerCase().contains("autoreply") }

			header[item] == value
		where:
			item               | value
			'title'            | "autoreply.title[fruits]"
			'autoreplyMessage' | 'Hello, this is an autoreply message'
	}

	def "clicking the edit option opens the Autoreply Dialog for editing"() {
		when:
			to PageMessageAutoreply, 'Fruits'
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("edit").jquery.click()
		then:
			waitFor("veryslow") { at AutoreplyCreateDialog }
	}

	def "Clicking the Quick Message button brings up the Quick Message Dialog"() {
		when:
			to PageMessageAutoreply, 'Fruits'
			waitFor { header.quickMessage.displayed }
			header.quickMessage.click()
		then:
			waitFor('veryslow'){ at QuickMessageDialog }
			waitFor{ textArea.displayed }
	}

	def "clicking the rename option opens the rename small popup"() {
		when:
			to PageMessageAutoreply, 'Fruits'
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
			to PageMessageAutoreply, 'Fruits'
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("delete").jquery.click()
		then:
			waitFor { at DeleteActivity }
	}

	def "clicking the export option opens the export dialog"() {
		when:
			to PageMessageAutoreply, 'Fruits'
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("export").jquery.click()
		then:
			waitFor { at ExportDialog }
	}

	def "selecting a single message reveals the single message view"() {
		when:
			to PageMessageAutoreply, 'Fruits'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "Test message 0" }
	}

	def "selecting multiple messages reveals the multiple message view"() {
		when:
			to PageMessageAutoreply, 'Fruits'
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
			to PageMessageAutoreply, 'Fruits'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(3)
		then:
			waitFor { singleMessageDetails.displayed }
			messageList.hasClass(3, "selected")
			singleMessageDetails.text == "Test message 3"
	}

	def "delete single message action works "() {
		when:
			to PageMessageAutoreply, 'Fruits'
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
			messageList.messageText(0) != 'Test message 0'
	}

	def "delete multiple message action works for multiple select"(){
		when:
			to PageMessageAutoreply, 'Fruits'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor { singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.deleteAll.click()
		then:
			waitFor { messageList.displayed }
			!(messageList.messageText(0) in ['Test message 0', 'Test message 1'])
			!(messageList.messageText(1) in ['Test message 0', 'Test message 1'])
	}

	def "move single message action works"() {
		when:
			to PageMessageAutoreply, 'Fruits'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "Test message 0" }
		when:
			singleMessageDetails.moveTo(remote { Activity.findByName("Sample Announcement").id }).click()
		then:
			waitFor("veryslow") { at PageMessageAutoreply }
			waitFor { notifications.flashMessageText.contains("updated") }
			messageList.messageText(0) != 'Test message 0'
		when:
			to PageMessageAnnouncement, 'Sample Announcement'
		then:
			messageList.messageText(0) == 'Test message 0'
	}

	def "move multiple message action works"() {
		when:
			to PageMessageAutoreply, 'Fruits'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor {singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.moveTo(remote { Activity.findByName("Sample Announcement").id }).click()
		then:
			waitFor("veryslow") { notifications.flashMessageText.contains("updated") }
			!(messageList.messageText(0) in ['Test message 0', 'Test message 1'])
			!(messageList.messageText(1) in ['Test message 0', 'Test message 1'])
		when:
			to PageMessageAnnouncement, 'Sample Announcement'
		then:
			waitFor { messageList.displayed }
			messageList.messageText(0) in ['Test message 0', 'Test message 1']
			messageList.messageText(1) in ['Test message 0', 'Test message 1']
	}

	def "moving a message from another activity to a autoreply displays an update message"() {
		setup:
			def activityId = remote { Activity.findByName("Sample Announcement").id }
			def mId = remote { TextMessage.findBySrc("announce").id }
			def autoreplyId = remote { Autoreply.findByName('Fruits').id }
		when:
			to PageMessageAnnouncement, activityId, mId
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.moveTo(autoreplyId)
		then:
			waitFor { flashMessage.displayed }
	}

	def "clicking on the sent message filter should display outgoing messages only"() {
		given:
			def a = createInAndOutTestMessages()
		when:
			to PageMessageAutoreply, a
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
			to PageMessageAutoreply, a
		then:
			messageList.messageCount() == 5
		when:
			footer.showIncoming.click()
		then:
			waitFor { messageList.messageCount() == 3 }
	}

	private createInAndOutTestMessages() {
		remote {
			Autoreply a = Autoreply.build(name:'Vegetables')
			3.times { a.addToMessages(TextMessage.build()) }
			2.times {
				def sentMessage = TextMessage.buildWithoutSave(inbound:false)
				sentMessage.addToDispatches(dst:'123456789', status:DispatchStatus.PENDING)
				sentMessage.save(failOnError:true, flush:true)
				a.addToMessages(sentMessage) }
			a.save(failOnError:true, flush:true)
			return a.id
		}
	}
}

