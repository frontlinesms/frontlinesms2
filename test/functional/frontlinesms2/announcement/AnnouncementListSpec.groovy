package frontlinesms2.announcement

import frontlinesms2.*
import java.text.SimpleDateFormat

class AnnouncementListSpec extends AnnouncementBaseSpec {
	private def DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyyy hh:mm", Locale.US)
	
	def 'Announcement message list is displayed'() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			go "message/announcement/${Announcement.findByName('New Office').id}"
			def announcementMessageSources = $('#messages tbody tr td:nth-child(3)')*.text()
		then:
			at PageMessageAnnouncementNewOffice
			announcementMessageSources == ['Jane', 'Max']
	}

	def 'no message is selected when a announcement is first loaded'() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			go "message/announcement/${Announcement.findByName('New Office').id}"
		then:
			$('#message-details #message-body').text() == "No message selected"
	}

	def "message's Announcement details are shown in list"() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			at PageMessageAnnouncementNewOffice
			def rowContents = $('#messages tbody tr:nth-child(2) td')*.text()
		then:
			rowContents[2] == 'Max'
			rowContents[3] == 'I will be late'
			rowContents[4] ==~ /[0-9]{2} [A-Z][a-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2}/
	}

	def 'selected announcement is highlighted'() {
		given:
			createTestAnnouncements()
		when:
			at PageMessageAnnouncementNewOffice
			def selectedMenuItem = $('#messages-menu .selected')
		then:
			selectedMenuItem.text() == 'New Office announcement'
	}

	def "should be able to reply for messages listed in the Announcement section"() {
		setup:
			createTestAnnouncements()
			createTestMessages()
		when:
			def announcement = Announcement.findByName("New Office")
			def messages = announcement.getMessages() as List
			def message = messages[0]
			go "message/announcement/${announcement.id}/show/${message.id}"
			$("#btn_reply").click()
		then:
			waitFor { $('div#tabs-1').displayed }
	}

	def "should filter announcement messages for starred and unstarred messages"() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			go "message/announcement/${Announcement.findByName('New Office').id}/show/${Fmessage.findBySrc('Max').id}"
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
			createTestAnnouncements()
			createTestMessages()
		when:
			go "message/announcement/${Announcement.findByName('New Office').id}/show/${Fmessage.findBySrc('Max').id}"
			def btnDropdown = $("#btn_dropdown")
		then:
			waitFor{ btnDropdown.displayed }
		when:
			btnDropdown.click()
			def btnForward = $("#btn_forward")
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
			createTestAnnouncements()
			createTestMessages()
		when:
			go "message/announcement/${Announcement.findByName('New Office').id}/show/${Fmessage.findBySrc('Max').id}"
		then:
			at PageMessageAnnouncementNewOffice
		when:
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $("#checked-message-count").text() == "2 messages selected" }
	}
	
	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createTestAnnouncements()
			createTestMessages()
			new Contact(name: 'Alice', primaryMobile: 'Alice').save(failOnError:true)
			new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
		when:
			go "message/announcement/${Announcement.findByName('New Office').id}/show/${Fmessage.findBySrc('Max').id}"
		then:
			at PageMessageAnnouncementNewOffice
		when:
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $('#multiple-messages a').displayed }
		when:
			btnReplyMultiple.click()
		then:
			waitFor { $('div#tabs-1').displayed }
	}
	
	def "can delete a announcement"() {
		when:
			deleteAnnouncement()
		then:
			$("title").text() == "Inbox"
			!$("a", text: "New Office")
	}
	
	def deleteAnnouncement() {
		createTestAnnouncements()
		createTestMessages()
		def announcement = Announcement.findByName("New Office")
		go "message/announcement/${announcement.id}"
		$("#announcement-actions").value("delete")
		waitFor { $("#ui-dialog-title-modalBox").displayed }
		$("#title").value("Delete announcement")
		$("#done").click()
		announcement
	}

}

