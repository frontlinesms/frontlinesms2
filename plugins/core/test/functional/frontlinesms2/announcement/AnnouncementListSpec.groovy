package frontlinesms2.announcement

import frontlinesms2.*
import java.text.SimpleDateFormat

class AnnouncementListSpec extends AnnouncementBaseSpec {
	private def DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US)
	
	def 'Announcement message list is displayed'() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncementNewOffice
			def announcementMessageSources = $('#message-list tbody tr .message-preview-sender a')*.text()
		then:
			announcementMessageSources == ['Jane', 'Max']
	}

	def 'no message is selected when a announcement is first loaded'() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			go "message/announcement/${Announcement.findByName('New Office').id}"
		then:
			$('#message-detail #message-detail-content').text() == "No message selected"
	}

	def "message's Announcement details are shown in list"() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncementNewOffice
			def rowContents = $('#messages tbody tr:nth-child(2) td')*.text()
		then:
			rowContents[2] == 'Max'
			rowContents[3] == 'I will be late'
			rowContents[4] ==~ /[0-9]{2} [A-Z][a-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2} [A-Z]{2}/
	}

	def 'selected announcement is highlighted'() {
		given:
			createTestAnnouncements()
		when:
			go "message/activity/${Announcement.findByName('New Office').id}"
			def selectedMenuItem = $('#sidebar .selected')
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
			go "message/activity/${announcement.id}/show/${message.id}"
			$("#btn_reply").click()
		then:
			waitFor { $('div#tabs-1').displayed }
	}

	def "should filter announcement messages for starred and unstarred messages"() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			go "message/activity/${Announcement.findByName('New Office').id}/show/${Fmessage.findBySrc('Max').id}"
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
		then:
			waitFor { $("#messages tbody tr").size() == 1 }
			$("#messages tbody tr")[0].find(".message-preview-sender").text() == 'Max'
		when:
			$('a', text:'All').click()
		then:
			waitFor { $("#messages tbody tr").size() == 2 }
			$("#messages tbody tr").collect {it.find(".message-preview-sender").text()}.containsAll(['Jane', 'Max'])
	}
	
	def "should autopopulate the message body when 'forward' is clicked"() {
		setup:
			createTestAnnouncements()
			createTestMessages()
		when:
			go "message/activity/${Announcement.findByName('New Office').id}/show/${Fmessage.findBySrc('Max').id}"
			$("#btn_forward").click()
		then:
			waitFor { $('div#tabs-1').displayed }
			$('textArea', name:'messageText').text() == "I will be late"
	}
	
	def "message count displayed when multiple messages are selected"() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncementNewOffice
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $("#checked-message-count").text() == "2 messages selected" }
	}
	
	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createTestAnnouncements()
			createTestMessages()
			new Contact(name: 'Alice', mobile: 'Alice').save(failOnError:true)
			new Contact(name: 'June', mobile: '+254778899').save(failOnError:true)
		when:
			to PageMessageAnnouncementNewOffice
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
			createTestAnnouncements()
			createTestMessages()
			def announcement = Announcement.findByName("New Office")
			to PageMessageAnnouncementNewOffice
			$(".button-list #more-actions").value("delete")
		then:
			waitFor { $("#ui-dialog-title-modalBox").displayed }
			$("#ui-dialog-title-modalBox").text().equalsIgnoreCase("Delete activity")
		when:
			$("#done").click()
		then:
			waitFor { $("#sidebar .selected").text() == "Inbox" }
			!$("a", text: "New Office announcement")
	}
}

