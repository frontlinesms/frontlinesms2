package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*
import frontlinesms2.announcement.*

import spock.lang.*

class WebconnectionViewSpec extends WebconnectionBaseSpec {
	def setup() {
		createWebconnections()
		createTestActivities()
		def wcId = remote { Webconnection.findByName("Sync").id }
		createTestMessages(wcId)
	}

	def "Webconnection page should show the details of a generic Webconnection in the header"() {
		setup:
			def webconnectionName  = remote { Webconnection.findByName("Sync").name }
		when:
			to PageMessageWebconnection, webconnectionName
		then:
			waitFor { title == 'webconnection.title[Sync]' }
			header.name == 'webconnection.title[sync]'
			header.url == 'http://www.frontlinesms.com/sync'
			header.sendMethod == 'get'
			header.subtitle == 'webconnection.generic.subtitle[generic]'
			header.api == 'webconnection.api.url : (webconnection.api.disabled)'
	}

	@Unroll
	def 'Webconnection page should show API url, excluding secret, iff API is enabled'() {
		setup:
			def webconnectionName = remote { GenericWebconnection.build(name:'me', apiEnabled:true, secret:secret, url:'http://test.com').name }
			def wcId = remote { GenericWebconnection.build(name:'me', apiEnabled:true, secret:secret, url:'http://test.com').id }
		when:
			to PageMessageWebconnection, webconnectionName
		then:
			header.api.endsWith "/api/1/webconnection/$wcId"
		where:
			secret << [null, 'imagine']
	}

	def "Webconnection page should show the details of an Ushahidi Webconnection in the header"() {
		setup:
			def webconnectionName  = remote { Webconnection.findByName("Ush").name }
		when:
			to PageMessageWebconnection, webconnectionName
		then:
			waitFor { title == 'webconnection.title[Ush]' }
			header.name == 'webconnection.title[ush]'
			header.url == 'http://www.ushahidi.com/frontlinesms'
			header.sendMethod == 'get'
			header.subtitle == 'webconnection.ushahidi.subtitle[ushahidi]'
	}

	def "clicking the archive button archives the Webconnection and redirects to inbox "() {
		when:
			to PageMessageWebconnection, "Sync"
		then:
			waitFor { header.displayed }
		when:
			header.archive.click()
		then:
			waitFor { at PageMessageInbox }
			notifications.flashMessageText == 'default.archived[activity.label]'
	}

	def "clicking the edit option opens the Webconnection Dialog for editing"() {
		when:
			to PageMessageWebconnection, "Sync"
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("edit").jquery.click()
		then:
			waitFor("veryslow") { at WebconnectionWizard }
	}

	def "Clicking the Send Message button brings up the Quick Message Dialog"() {
		when:
			to PageMessageWebconnection, "Sync"
			waitFor { header.quickMessage.displayed }
			header.quickMessage.click()
		then:
			waitFor('veryslow'){ at QuickMessageDialog }
			waitFor{ textArea.displayed }
	}

	def "clicking the rename option opens the rename small popup"() {
		when:
			to PageMessageWebconnection, "Sync"
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("rename").jquery.click()
		then:
			waitFor { at RenameDialog }
			name.jquery.val().contains("Sync")
	}

	def "clicking the delete option opens the confirm delete small popup"() {
		when:
			to PageMessageWebconnection, "Sync"
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("delete").jquery.click()
		then:
			waitFor { at DeleteActivity }
	}

	def "clicking the export option opens the export dialog"() {
		when:
			to PageMessageWebconnection, "Sync"
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("export").jquery.click()
		then:
			waitFor { at ExportDialog }
	}

	def "selecting a single message reveals the single message view"() {
		when:
			to PageMessageWebconnection, "Sync"
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
			to PageMessageWebconnection, "Sync"
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor { singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
			multipleMessageDetails.checkedMessageCount == 2
	}

	def "clicking on a message reveals the single message view with clicked message"() {
		when:
			to PageMessageWebconnection, "Sync"
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(3)
		then:
			waitFor { singleMessageDetails.displayed }
			messageList.hasClass(3, "selected")
			waitFor { singleMessageDetails.text == "Test message 3" }
	}

	def "delete single message action works "() {
		when:
			to PageMessageWebconnection, "Sync"
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
			to PageMessageWebconnection, "Sync"
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
			to PageMessageWebconnection, "Sync"
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
			waitFor("veryslow") { at PageMessageWebconnection }
			waitFor { notifications.flashMessageText.contains("updated") }
			messageList.messageText(0) != 'Test message 0'
		when:
			to PageMessageAnnouncement, "Sample Announcement"
		then:
			waitFor { messageList.displayed }
			messageList.messageText(0) == 'Test message 0'
	}

	def "move multiple message action works"() {
		when:
			to PageMessageWebconnection, "Sync"
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
			!(messageList.messageText(0) in ['Test message 0', 'Test message 1'])
			!(messageList.messageText(1) in ['Test message 0', 'Test message 1'])
		when:
			to PageMessageAnnouncement, "Sample Announcement"
		then:
			waitFor { messageList.displayed }
			messageList.messageText(0) in ['Test message 0', 'Test message 1']
			messageList.messageText(1) in ['Test message 0', 'Test message 1']
	}

	def "should display SENT message status for successfully forwarded messages"() {
		when:
			to PageMessageWebconnection, "Sync"
		then:
			waitFor { messageList.displayed }
			js.exec '''
				return $("#main-list tbody tr.ownerdetail-webconnection-SENT").size() > 0
			'''
	}

	def "retry failed uploads option should be present in more actions dropdown, and should redirect to same view"() {
		when:
			to PageMessageWebconnection, "Sync"
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value('retryFailed')
		then:
			waitFor { notifications.flashMessageText == 'webconnection.failed.retried' }
			at PageMessageWebconnection
	}
}

