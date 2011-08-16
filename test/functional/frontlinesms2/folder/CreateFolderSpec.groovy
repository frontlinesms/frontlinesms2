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

	def 'existing folders appear in activities section of messages'() {
			given:
				createTestFolders()
			when:
				go 'message'
			then:
				$('#folders-submenu li')[0].text().contains('Work')
				$('#folders-submenu li')[1].text().contains('Project')
			cleanup:
				deleteTestFolders()
	}

// FIXME
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
