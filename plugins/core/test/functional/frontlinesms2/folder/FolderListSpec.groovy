package frontlinesms2.folder

import frontlinesms2.*
import java.text.SimpleDateFormat
import frontlinesms2.message.*
import frontlinesms2.popup.*
import spock.lang.*

class FolderListSpec extends FolderBaseSpec {
	def trashService = new TrashService()
	private def DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US)

	def 'folder message list is displayed'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolder, Folder.findByName('Work')
		then:
			messageList.sources.containsAll(['Jane', 'Max'])
	}

	def 'no message is selected when a folder is first loaded'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolder, Folder.findByName('Work')
		then:
			singleMessageDetails.noneSelected
	}

	def "message's folder details are shown in list"() {
		given:
			createTestFolders()
			createTestMessages()
			def folder = Folder.findByName("Work")
		when:
			to PageMessageFolder, folder
		then:
			messageList.messages[1].source == 'Max'
			messageList.messages[1].text == 'I will be late'
			DATE_FORMAT.format(messageList.messages[1].date) ==~ /[0-9]{2} [A-Za-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2} [A-Z]{2}/
	}

	def 'selected folder is highlighted'() {
		given:
			createTestFolders()
		when:
			to PageMessageFolder, Folder.findByName('Work')
		then:
			bodyMenu.selected == 'work'
	}

	def "should be able to reply for messages listed in the folder section"() {
		setup:
			createTestFolders()
			createTestMessages()
			def folder = Folder.findByName("Work")
			def messages = folder.getMessages() as List
			def message = messages[0]
		when:
			to PageMessageFolder, folder, message
			singleMessageDetails.reply.jquery.trigger("click")
		then:
			waitFor { at QuickMessageDialog }
	}

	def "should filter folder messages for starred and unstarred messages"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolder, Folder.findByName('Work'), Fmessage.findBySrc('Max')
		then:
			messageList.messages.size() == 2
		when:
			footer.showStarred.jquery.trigger("click")
		then:
			waitFor { messageList.messages.size() == 2 }
			messageList.messages[1].source == 'Max'
		when:
			footer.showAll.jquery.trigger("click")
		then:
			waitFor { messageList.messages.size() == 2 }
			messageList.sources.containsAll(['Jane', 'Max'])
	}

	def "should autopopulate the message body when 'forward' is clicked"() {
		setup:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolder, Folder.findByName('Work'), Fmessage.findBySrc('Max')
			singleMessageDetails.forward.jquery.trigger("click")
		then:
			waitFor { at QuickMessageDialog }
			at QuickMessageDialog
			waitFor { compose.textArea.text() == "I will be late" }
	}

	def "message count displayed when multiple messages are selected"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolder, Folder.findByName('Work')
			messageList.messages[0].checkbox.click()
			waitFor("slow") { singleMessageDetails.displayed }
			messageList.messages[1].checkbox.click()
		then:
			waitFor { multipleMessageDetails.displayed }
			waitFor { multipleMessageDetails.checkedMessageCount == "2 messages selected" }
	}
	
	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createTestFolders()
			createTestMessages()
			new Contact(name: 'Alice', mobile: 'Alice').save(failOnError:true)
			new Contact(name: 'June', mobile: '+254778899').save(failOnError:true)
		when:
			to PageMessageFolder, Folder.findByName('Work')
			messageList.messages[0].checkbox.click()
			messageList.messages[1].checkbox.click()
			multipleMessageDetails.replyAll.jquery.trigger("click")
		then:
			waitFor { at QuickMessageDialog }
	}

	def "can rename a folder"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolder, Folder.findByName('Work')
			folderMoreActions.value("rename")
			waitFor { at RenameFolderDialog }
			folderName.equals("Work")
			folderName.value("New Work")
			ok.jquery.trigger("click")
			to PageMessageFolder
		then:
			folderLinks*.text().containsAll('New Work', 'Projects')
	}

	def 'Errors are displayed when creating a folder with an already existing folders name'() {
			given:
				createTestFolders()
				createTestMessages()
			when:
				to PageMessageFolder
				bodyMenu.newFolder.jquery.trigger("click")
			then:
				waitFor { at CreateFolderPopup }
			when:
				folderName.value("Work")
				ok.jquery.trigger("click")
			then:
				at CreateFolderPopup
				waitFor { errorPanel.text().toLowerCase() == "used folder name" }
	}

	def "display error when renaming a folder with a blank name"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolder, Folder.findByName('Work')
			folderMoreActions.value("rename")
			waitFor { at RenameFolderDialog }
			folderName.equals("Work")
			folderName.value("")
			ok.jquery.trigger("click")
		then:
			waitFor { errorPanel.text().toLowerCase() == "folder name cannot be blank" }
	}

	def "can delete a folder"() {
		setup:
			createTestFolders()
		when:
			to PageMessageFolder, Folder.findByName("Work")
			folderMoreActions.value("delete")
			waitFor { at DeleteFolderPopup }
			popupTitle.equals("delete folder")
		then:
			ok.jquery.trigger("click")
			at PageMessageFolder
			waitFor { bodyMenu.selected == "inbox" }
			!bodyMenu.folderLinks*.text().containsAll('Work')
	}
	
	def "deleted folders show up in the trash section"() {
		setup:
			def folderId = deleteFolder()
		when:
			at PageMessageFolder
			to PageMessageTrash, Trash.findByObjectId(folderId).id
		then:
			messageList.messages[0].source == 'Work'
			messageList.messages[0].text == '2 message(s)'
			DATE_FORMAT.format(messageList.messages[0].date) == DATE_FORMAT.format(Trash.findByObjectId(folderId).dateCreated)
			senderDetails == 'Work folder'
			DATE_FORMAT.format(date) == DATE_FORMAT.format(Trash.findByObjectId(folderId).dateCreated)
	}

	def "clicking on empty trash permanently deletes a folder"() {
		setup:
			def folderId = deleteFolder()
		when:
			at PageMessageFolder
			to PageMessageTrash, Trash.findByObjectId(folderId).id
			trashMoreActions.value("empty-trash")
			waitFor { at EmptyTrashPopup }
		then:
			popupTitle.equals("empty trash?")
		when:
			ok.jquery.trigger("click")
			waitFor { title.toLowerCase().contains("inbox") }
		then:
			at PageMessageInbox
			bodyMenu.folderLinks*.text().containsAll('Projects')
	}
	
	def deleteFolder() {
		createTestFolders()
		createTestMessages()
		def folder = Folder.findByName("Work")
		trashService.sendToTrash(folder)
		folder.id
	}
}

