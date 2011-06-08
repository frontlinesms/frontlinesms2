package frontlinesms2.folder

import frontlinesms2.*

class CreateFolderSpec extends FolderGebSpec {
	def 'button to create new folder from messages links to create folder page'() {
		when:
			go 'message'
			def btnNewPoll = $('#create-folder a')
		then:
			btnNewPoll.getAttribute('href') == "/frontlinesms2/folder/create"
	}

def 'button to save new folder works'() {
		given:
			createTestFolders()
			def initialFolderCount = Folder.count()
		when:
			to CreateFolderPage
			frmDetails.value = 'Fun'
			btnSave.click()
		then:
			Folder.count() == initialFolderCount + 1
			title.contains("Inbox")
		cleanup:
			deleteTestFolders() 
	}
	
def 'existing folders appear in activities section of messages'() {
		given:
			createTestFolders()
		when:
			go 'message'
		then:
			$('#activities-submenu li')*.text() == ['Work', 'Projects']
		cleanup:
			deleteTestFolders()
	}
	
def 'Errors are displayed when folder fails to save'() {
		when:
			to CreateFolderPage
			btnSave.click()
		then:
			errorMessages.present
	}
}

class CreateFolderPage extends geb.Page {
	static url = 'folder/create'
	static at = {
		// FIXME put in a test here
		true
	}
	static content = {
		frmDetails { $("#folder-details") }
		btnSave { $('input', name:'save') }
		errorMessages(required:false) { $('.flash.errors') }
	}
}
