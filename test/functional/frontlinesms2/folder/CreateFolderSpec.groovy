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

	def "'messages' menu item is selected when creating a new folder"() {
		when:
			go 'folder/create'
		then:
			$('#tab-messages').hasClass('selected')
	}
	
	def 'button to save new folder works'() {
			given:
				createTestFolders()
				def initialFolderCount = Folder.count()
			when:
				go "folder/create"
				def frmDetails = $("#folder-details")
				def btnSave = $('input', name:'save')
				frmDetails.name = 'Fun'
				btnSave.click()
                waitFor { !($("div.flash.message").text().isEmpty()) }
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

//
//	def 'Errors are displayed when folder fails to save'() {
//			when:
//				to CreateFolderPage
//				def btnSave = $('input', name:'save')
////				def errorMessages(required:false) { $('.flash.errors') }
//				btnSave.click()
//			then:
//				errorMessages.present
//	}
}

class CreateFolderPage extends geb.Page {
	static url = 'folder/create'
	static at = {
		// FIXME put in a test here
		true
	}
	static content = {
//		frmDetails { $("#folder-details") }
//		btnSave { $('input', name:'save') }
//		errorMessages(required:false) { $('.flash.errors') }
	}
}
