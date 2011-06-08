package frontlinesms2.message

import frontlinesms2.*

class FolderListSpec extends frontlinesms2.folder.FolderGebSpec {
	def 'folder message list is displayed'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to FolderListPage
			def folderMessageSources = $('#messages tbody tr td:first-child')*.text()
		then:
			at FolderListPage
			folderMessageSources == ['Max', 'Jane']
		cleanup:
			deleteTestFolders()
	}

	def "message's folder details are shown in list"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to FolderListPage
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			rowContents[0] == 'Max'
			rowContents[1] == 'I will be late'
			rowContents[2] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
		cleanup:
			deleteTestFolders()
	}

	def 'selected folder is highlighted'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to FolderListPage
		then:
			selectedMenuItem.text() == 'Work'
		cleanup:
			deleteTestFolders()
	}
}

class FolderListPage extends geb.Page {
 	static getUrl() { "message/folder/${Folder.findByValue('Work').id}" }
	static at = {
		title.endsWith('Folder')
	}
	static content = {
		selectedMenuItem { $('#messages-menu .selected') }
		messagesList { $('#messages-submenu') }
	}
}