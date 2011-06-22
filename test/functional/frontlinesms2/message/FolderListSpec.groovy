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
			folderMessageSources == ['Jane', 'Max']
		cleanup:
			deleteTestFolders()
	}

	def "message's folder details are shown in list"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			"message/folder/${Folder.findByValue('Work').id}/show/${Fmessage.findBySrc('Max')}"
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			rowContents[0] == 'Jane'
			rowContents[1] == 'Meeting at 10 am'
			rowContents[2] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
		cleanup:
			deleteTestFolders()
	}

	def 'selected folder is highlighted'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			"message/folder/${Folder.findByValue('Work').id}/show/${Fmessage.findBySrc('Max')}"
			def selectedMenuItem = $('#messages-menu .selected')
		then:
			selectedMenuItem.text() == 'Work'
		cleanup:
			deleteTestFolders()
	}
}

class FolderListPage extends geb.Page {
 	static url = "message/folder/${Folder.findByValue('Work').id}/show/${Fmessage.findBySrc('Max').id}"
	static at = {
		title.endsWith('Folder')
	}
	static content = {
		messagesList { $('#messages-submenu') }
	}
}