package frontlinesms2.folder

import frontlinesms2.*
import java.text.SimpleDateFormat

class FolderListSpec extends FolderBaseSpec {
	private def DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US)
	
	def 'folder message list is displayed'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolderWork
			def folderMessageSources = $('#messages tbody tr .message-preview-sender a')*.text()
		then:
			folderMessageSources == ['Jane', 'Max']
	}

	def 'no message is selected when a folder is first loaded'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			go "message/folder/${Folder.findByName('Work').id}"
		then:
			$('#message-detail #message-detail-content').text() == "No message selected"
	}

	def "message's folder details are shown in list"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			at PageMessageFolderWork
			def rowContents = $('#messages tbody tr:nth-child(2) td')*.text()
		then:
			rowContents[2] == 'Max'
			rowContents[3] == 'I will be late'
			rowContents[4] ==~ /[0-9]{2} [A-Z][a-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2} [A-Z]{2}/
	}

	def 'selected folder is highlighted'() {
		given:
			createTestFolders()
		when:
			at PageMessageFolderWork
			def selectedMenuItem = $('#sidebar .selected')
		then:
			selectedMenuItem.text() == 'Work'
	}

	def "should be able to reply for messages listed in the folder section"() {
		setup:
			createTestFolders()
			createTestMessages()
		when:
			def folder = Folder.findByName("Work")
			def messages = folder.getMessages() as List
			def message = messages[0]
			go "message/folder/${folder.id}/show/${message.id}"
			$("#btn_reply").click()
		then:
			waitFor { $('div#tabs-1').displayed }
	}

	def "should filter folder messages for starred and unstarred messages"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			go "message/folder/${Folder.findByName('Work').id}/show/${Fmessage.findBySrc('Max').id}"
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
		then:
			waitFor { $("#messages tbody tr").size() == 1 }
			$("#messages tbody tr .message-preview-sender a")[0].text() == 'Max'
		when:
			$('a', text:'All').click()
		then:
			waitFor { $("#messages tbody tr").size() == 2 }
			$("#messages tbody tr .message-preview-sender a")*.text().containsAll(['Jane', 'Max'])
	}
	
	def "should autopopulate the message body when 'forward' is clicked"() {
		setup:
			createTestFolders()
			createTestMessages()
		when:
			go "message/folder/${Folder.findByName('Work').id}/show/${Fmessage.findBySrc('Max').id}"
			def btnDropdown = $("#btn_dropdown")
		then:
			waitFor{ btnDropdown.displayed }
		when:
			btnDropdown.click()
			def btnForward = $("#btn_forward")
		then:
			waitFor{ btnForward.displayed }
		when:
			btnForward.click()
		then:
			waitFor { $('div#tabs-1').displayed }
			$('textArea', name:'messageText').text() == "I will be late"
	}
	
	def "message count displayed when multiple messages are selected"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			go "message/folder/${Folder.findByName('Work').id}/show/${Fmessage.findBySrc('Max').id}"
		then:
			at PageMessageFolderWork
		when:
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $("#checked-message-count").text() == "2 messages selected" }
	}
	
	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createTestFolders()
			createTestMessages()
			new Contact(name: 'Alice', primaryMobile: 'Alice').save(failOnError:true)
			new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
		when:
			go "message/folder/${Folder.findByName('Work').id}/show/${Fmessage.findBySrc('Max').id}"
		then:
			at PageMessageFolderWork
		when:
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $('#multiple-messages a').displayed }
		when:
			btnReplyMultiple.click()
		then:
			waitFor { $('div#tabs-1').displayed }
	}
	
	def "can delete a folder"() {
		when:
			deleteFolder()
		then:
			waitFor { $("#sidebar .selected").text() == "Inbox" }
			!$("a", text: "Work")
	}
	
	def "deleted folders show up in the trash section"() {
		setup:
			def folderId = deleteFolder()
		when:
			go "message/trash/show/${Trash.findByLinkId(folderId).id}"
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			rowContents[2] == 'Work'
			rowContents[3] == '2 messages'
			rowContents[4] == DATE_FORMAT.format(Trash.findByLinkId(folderId).dateCreated)
			$('#message-detail-sender').text() == 'Work folder'
			$('#message-detail-date').text() == DATE_FORMAT.format(Trash.findByLinkId(folderId).dateCreated)
			$('#message-detail-content').text() == "${Folder.findById(folderId).getLiveMessageCount()} messages"
	}
	
	def "clicking on empty trash permanently deletes a folder"() {
		setup:
			def folderId = deleteFolder()
		when:
			go "message/trash"
			$("#trash-actions").value("empty-trash")
		then:
			waitFor { $("#ui-dialog-title-modalBox").displayed }
		when:
			$("#title").value("Empty trash")
			$("#done").click()
		then:
			!Folder.findById(folderId)
	}
	
	def deleteFolder() {
		createTestFolders()
		createTestMessages()
		def folderId = Folder.findByName("Work").id
		go "message/folder/${folderId}"
		$(".button-list #more-actions").value("delete")
		waitFor { $("#ui-dialog-title-modalBox").displayed }
		$("#ui-dialog-title-modalBox").text().equalsIgnoreCase("Delete folder")
		$("#done").click()
		folderId
	}

}

