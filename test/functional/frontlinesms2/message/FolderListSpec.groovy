package frontlinesms2.message

import frontlinesms2.*

class FolderListSpec extends frontlinesms2.folder.FolderGebSpec {
	def 'folder message list is displayed'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to FolderListPage
			def folderMessageSources = $('#messages tbody tr td:nth-child(3)')*.text()
		then:
			at FolderListPage
			folderMessageSources == ['Jane', 'Max']
	}
	
	def "message's folder details are shown in list"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to FolderListPage
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			rowContents[2] == 'Jane'
			rowContents[3] == 'Meeting at 10 am'
			rowContents[4] ==~ /[0-9]{2} [A-Z][a-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2}/
	}

	def 'selected folder is highlighted'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to FolderListPage
			def selectedMenuItem = $('#messages-menu .selected')
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
			waitFor {$('div#tabs-1').displayed}
		when:
			$("div#tabs-1 .next").click()
		then:
			$('input', value: message.src).@checked
	}

	def "should filter folder messages for starred and unstarred messages"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to FolderListPage
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
			waitFor {$("#messages tbody tr").size() == 1}
		then:
			$("#messages tbody tr")[0].find("td:nth-child(3)").text() == 'Max'
		when:
			$('a', text:'All').click()
		then:
			waitFor { $("#messages tbody tr").size() == 2 }
			$("#messages tbody tr").collect {it.find("td:nth-child(3)").text()}.containsAll(['Jane', 'Max'])
	}
	
	def "should autopopulate the message body when 'forward' is clicked"() {
		setup:
			createTestFolders()
			createTestMessages()
		when:
			to FolderListPage
		then:
			waitFor{ $("#btn_dropdown").displayed }
		when:
			$("#btn_dropdown").click()
		then:
			waitFor{ $("#btn_forward").displayed }
		when:
			$("#btn_forward").click()
		then:
			waitFor { $('div#tabs-1').displayed }
			$('textArea', name:'messageText').text() == "I will be late"
	}
	
	def "message count displayed when multiple messages are selected"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to FolderListPage
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
			to FolderListPage
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { btnReplyMultiple.displayed }
		when:
			btnReplyMultiple.click()
		then:
			waitFor { $('div#tabs-1').displayed }
		when:
			$("div#tabs-1 .next").click()
		then:
			$('input', value:'Max').@checked
			$('input', value:'Jane').@checked
			!$('input', value:'June').@checked
	}

}

class FolderListPage extends geb.Page {
 	static def getUrl() { "message/folder/${Folder.findByName('Work').id}/show/${Fmessage.findBySrc('Max').id}" }
	static at = {
		title.endsWith('Folder')
	}
	static content = {
		messagesList { $('#messages-submenu') }
		messagesSelect { $(".message-select") }
		btnReplyMultiple { $('#multiple-messages a')[0] }
	}
}
