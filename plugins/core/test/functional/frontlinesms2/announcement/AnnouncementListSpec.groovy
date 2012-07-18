package frontlinesms2.announcement

import frontlinesms2.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.page.PageMessageActivity
import java.text.SimpleDateFormat

class AnnouncementListSpec extends AnnouncementBaseSpec {
	private def DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US)
	
	def 'Announcement message list is displayed'() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncement, 'New Office'
			def announcementMessageSources = messageSender.find('a')*.text()
		then:
			announcementMessageSources.containsAll(['Jane', 'Max'])
	}

	def "message's Announcement details are shown in list"() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncement, 'New Office'
		then:
			rowContents[2] == 'Max'
			rowContents[3] == 'I will be late'
			rowContents[4] ==~ /[0-9]{2} [A-Za-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2} [A-Z]{2}/
	}

	def 'selected announcement is highlighted'() {
		given:
			createTestAnnouncements()
		when:
			to PageMessageAnnouncement, 'New Office'
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
			to PageMessageAnnouncement, 'New Office', Fmessage.findBySrc('Max')
			singleMessageDetails.reply.click()
		then:
			waitFor {at QuickMessageDialog}
	}

	def "should filter announcement messages for starred and unstarred messages"() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncement, 'New Office'
		then:
			messagesList.size() == 3
		when:
			footer.showStarred.click()
		then:
			waitFor { messagesList.size() == 2 }
			messagesList[1].find(".message-sender-cell").text() == 'Max'
		when:
			footer.showAll.click()
		then:
			waitFor { messagesList.size() == 3 }
			messagesList.collect {it.find(".message-sender-cell").text()}.containsAll(['Jane', 'Max'])
	}
	
	def "should autopopulate the message body when 'forward' is clicked"() {
		setup:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncement, 'New Office', Fmessage.findBySrc('Max')
			singleMessageDetails.forward.click()
		then:
			waitFor { at QuickMessageDialog }
			compose.textArea.text() == "I will be late"
	}

	def "message count displayed when multiple messages are selected"() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncement, 'New Office'
			messagesSelect[1].click()
		then:
			waitFor { singleMessageDetails.sender.contains("Jane") }
		when:
			messagesSelect[2].click()
		then:
			waitFor { multipleMessageDetails.messageCount.text() == "2 messages selected" }
	}

	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createTestAnnouncements()
			createTestMessages()
			new Contact(name: 'Alice', mobile: 'Alice').save(failOnError:true)
			new Contact(name: 'June', mobile: '+254778899').save(failOnError:true)
		when:
			to PageMessageAnnouncement, 'New Office'
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { multipleMessageDetails.replyAll.displayed }
		when:
			multipleMessageDetails.replyAll.click()
		then:
			waitFor { at QuickMessageDialog }
	}

	def "can delete a announcement"() {
		when:
			createTestAnnouncements()
			createTestMessages()
			to PageMessageAnnouncement, 'New Office'
			moreActions.value("delete")
		then:
			waitFor { at DeleteActivity }
		when:
			ok.click()
		then:
			at PageMessageInbox
			!bodyMenu.activityList.text().contains('New Office')
	}
}

