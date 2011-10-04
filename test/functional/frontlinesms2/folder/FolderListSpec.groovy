package frontlinesms2.folder

import frontlinesms2.*

class FolderListSpec extends FolderBaseSpec {
	def 'folder message list is displayed'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolderWork
			def folderMessageSources = $('#messages tbody tr td:nth-child(3)')*.text()
		then:
			at PageMessageFolderWork
			folderMessageSources == ['Max', 'Jane']
	}
	
	def "message's folder details are shown in list"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolderWork
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			rowContents[2] == 'Max'
			rowContents[3] == 'I will be late'
			rowContents[4] ==~ /[0-9]{2} [A-Z][a-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2}/
	}

	def 'selected folder is highlighted'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolderWork
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
			waitFor { $('div#tabs-1').displayed }
	}

	def "should filter folder messages for starred and unstarred messages"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolderWork
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
		then:
			waitFor { $("#messages tbody tr").size() == 1 }
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
			to PageMessageFolderWork
		then:
			waitFor{ btnDropdown.displayed }
		when:
			btnDropdown.click()
		then:
			waitFor{ btnForward.displayed }
		when:
			btnForward.click()
		then:
			waitFor { $('div#tabs-1').displayed }
			$('textArea', name:'messageText').text() == "I will be late"
	}
	
	def "message count displayed when multiple messages are selected"() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			to PageMessageFolderWork
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
			to PageMessageFolderWork
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { btnReplyMultiple.displayed }
		when:
			btnReplyMultiple.click()
		then:
			waitFor { $('div#tabs-1').displayed }
	}

}

