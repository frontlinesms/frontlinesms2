package frontlinesms2.folder

import frontlinesms2.*
import frontlinesms2.popup.*

class FolderCedSpec extends FolderBaseSpec {
	
	def 'button to create new folder from messages links to create folder page'() {
		when:
			to PageMessageFolder
		then:
			bodyMenu.newFolder.@href == "/folder/create"
	}

	def 'existing folders appear in activities section of messages'() {
			given:
				createTestFolders()
			when:
				to PageMessageFolder
			then:
				folderLinks*.text().containsAll('Work', 'Projects')
	}

	def 'Errors are displayed when folder fails to save'() {
			when:
				to PageMessageFolder
				bodyMenu.newFolder.jquery.trigger("click")
			then:
				waitFor { at CreateFolderPopup }
			when:
				ok.jquery.trigger("click")
			then:
				waitFor { errorPanel.text()?.toLowerCase() == "folder.name.blank.error" }
	}

}
