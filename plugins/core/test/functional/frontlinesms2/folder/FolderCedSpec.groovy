package frontlinesms2.folder

import frontlinesms2.*

class FolderCedSpec extends FolderBaseSpec {
	
	def 'button to create new folder from messages links to create folder page'() {
		when:
			go 'message'
		then:
			$('#create-folder a').@href == "/folder/create"
	}

	def 'existing folders appear in activities section of messages'() {
			given:
				createTestFolders()
			when:
				go 'message'
			then:
				$('#folders-submenu li')*.text().containsAll('Work', 'Projects')
	}

	def 'Errors are displayed when folder fails to save'() {
			when:
				go 'message'
				$('#create-folder a').click()
			then:
				waitFor { $("div.ui-dialog").displayed }
			when:
				$('#done').click()
			then:
				waitFor { $('.flash.message').displayed }
	}
}
