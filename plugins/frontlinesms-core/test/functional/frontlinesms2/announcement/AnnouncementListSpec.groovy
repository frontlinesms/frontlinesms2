package frontlinesms2.announcement

import frontlinesms2.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.page.PageMessageActivity

class AnnouncementListSpec extends AnnouncementBaseSpec {
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
			rowContents[4] ==~ /[0-9]{4}-[0-9]{2}-[0-9]{2}_[0-9]{2}:[0-9]{2}/
	}

	def 'selected announcement is highlighted'() {
		given:
			createTestAnnouncements()
		when:
			to PageMessageAnnouncement, 'New Office'
		then:
			selectedMenuItem.text() == 'announcement.title[New Office]'
	}

	def "should be able to reply for messages listed in the Announcement section"() {
		setup:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncement, 'New Office', remote { TextMessage.findBySrc('Max').id }
			singleMessageDetails.reply.click()
		then:
			waitFor { at QuickMessageDialog }
	}

	def "should filter announcement messages for starred and unstarred messages"() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncement, 'New Office'
		then:
			messagesList.size() == 3
			footer.showAll.hasClass('active')
			!footer.showStarred.hasClass('active')
		when:
			footer.showStarred.click()
		then:
			waitFor { messagesList.size() == 2 }
			messagesList[1].find(".message-sender-cell").text() == 'Max'
			!footer.showAll.hasClass('active')
			footer.showStarred.hasClass('active')
		when:
			footer.showAll.click()
		then:
			waitFor { messagesList.size() == 3 }
			messagesList.collect { it.find(".message-sender-cell").text() }.containsAll(['Jane', 'Max'])
			footer.showAll.hasClass('active')
			!footer.showStarred.hasClass('active')
	}
	
	def "should autopopulate the message body when 'forward' is clicked"() {
		setup:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncement, 'New Office', remote { TextMessage.findBySrc('Max').id }
			singleMessageDetails.forward.click()
		then:
			waitFor { at QuickMessageDialog }
			textArea.text() == "I will be late"
	}

	def "message count displayed when multiple messages are selected"() {
		given:
			createTestAnnouncements()
			createTestMessages()
		when:
			to PageMessageAnnouncement, 'New Office'
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.sender.contains("Jane") }
		when:
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
			waitFor { multipleMessageDetails.checkedMessageCount == 2 }
	}

	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createTestAnnouncements()
			createTestMessages()
			remote {
				new Contact(name: 'Alice', mobile: 'Alice').save(failOnError:true, flush:true)
				new Contact(name: 'June', mobile: '+254778899').save(failOnError:true, flush:true)
				null
			}
		when:
			to PageMessageAnnouncement, 'New Office'
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
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
			!bodyMenu.activityList.text()?.contains('New Office')
	}
}

