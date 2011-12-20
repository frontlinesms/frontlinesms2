package frontlinesms2.folder

import frontlinesms2.*

class FolderCedSpec extends FolderBaseSpec {
	
	def 'button to create new folder from messages links to create folder page'() {
		when:
			go 'message'
			def btnNewFolder = $('#create-folder a')
		then:
			btnNewFolder.getAttribute('href').contains("/folder/create")
	}

	def 'existing folders appear in activities section of messages'() {
			given:
				createTestFolders()
			when:
				go 'message'
			then:
				$('#folders-submenu li')[0].text().contains('Work')
				$('#folders-submenu li')[1].text().contains('Project')
	}

// FIXME
//	def 'Errors are displayed when folder fails to save'() {
//			when:
//				to PageFolderCreate
//				def btnSave = $('input', name:'save')
////				def errorMessages(required:false) { $('.flash.errors') }
//				btnSave.click()
//			then:
//				errorMessages.present
//	}
}
