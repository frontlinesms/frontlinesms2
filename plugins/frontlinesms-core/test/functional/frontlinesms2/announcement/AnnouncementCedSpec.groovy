package frontlinesms2.announcement

import frontlinesms2.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.popup.*

class AnnouncementCedSpec extends AnnouncementBaseSpec {

	def "can launch announcement screen from create new activity link" () {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			announcement.click()
		then:
			waitFor('slow') { at AnnouncementDialog }
	}

	def "can create a new Announcement" () {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			announcement.click()
		then:
			waitFor { at AnnouncementDialog }
		when:
			composeAnnouncement.textArea.value("announcing this new announcement!")
			next.click()
		then:
			waitFor { recipients.addField.displayed }
		when:
			recipients.addField.value("+919544426000")
			recipients.addButton.click()
			next.click()
		then:
			waitFor { confirm.announcementName.displayed }
		when:
			confirm.announcementName.value("newbie")
			submit.click()
		then:
			waitFor { summary.message.displayed }
	}

	def 'existing announcements appear in activities section of messages'() {
			given:
				createTestAnnouncements()
			when:
				to PageMessageInbox
			then:
				waitFor('slow') {
					bodyMenu.activityList*.text().containsAll(['announcement.title[New Office]', 'announcement.title[Office Party]'])
				}
	}

	def "should display errors when announcement validation fails"() {
		setup:
			remote {
				new Announcement(name: "newbie", messageText: "announcing this new announcement!", messages:[]).save(failOnError:true, flush:true)
				null
			}
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			announcement.click()
		then:
			waitFor { at AnnouncementDialog }
		when:
			composeAnnouncement.textArea.value("announcing this new announcement!")
			next.click()
		then:
			waitFor { recipients.addField.displayed }
		when:
			recipients.addField.value("+919544426000")
			recipients.addButton.click()
			next.click()
		then:
			waitFor { confirm.announcementName.displayed }
		when:
			confirm.announcementName.value("newbie")
			submit.click()
		then:
			remote { Announcement.count() == 1 }
			waitFor { error }
			at AnnouncementDialog
	}

	def "should be able to use non-English characters in wizard"() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			announcement.click()
		then:
			waitFor { at AnnouncementDialog }
		when:
			composeAnnouncement.textArea.value("박지성")
			next.click()
		then:
			waitFor { recipients.addField.displayed }
		when:
			recipients.addField.value("+919544426000")
			recipients.addButton.click()
			next.click()
		then:
			waitFor { confirm.announcementName.displayed }
		when:
			confirm.announcementName.value("香川真司")
			submit.click()
		then:
			waitFor { remote { Announcement.count() == 1 } }
	}
}

