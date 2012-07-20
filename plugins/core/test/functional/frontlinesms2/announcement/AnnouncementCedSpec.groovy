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
			waitFor { at CreateActivityDialog}
		when:
			announcement.click()
		then:
			waitFor { at AnnouncementDialog }
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
			waitFor {recipients.addField.displayed}
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
			waitFor { summary.displayed}
	}

	def 'existing announcements appear in activities section of messages'() {
			given:
				createTestAnnouncements()
			when:
				to PageMessageInbox
			then:
				waitFor('slow') {
					bodyMenu.activityList[0].text().contains('New Office')
					bodyMenu.activityList[1].text().contains('Office Party')
				}
	}

	def "should display errors when announcement validation fails"() {
		setup:
			def announcementNewbie = new Announcement(name: "newbie", messageText: "announcing this new announcement!", messages:[]).save(failOnError:true)
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog}
		when:
			announcement.click()
		then:
			waitFor { at AnnouncementDialog }
		when:
			composeAnnouncement.textArea.value("announcing this new announcement!")
			next.click()
		then:
			waitFor {recipients.addField.displayed}
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
			assert Announcement.count() == 1
			waitFor { error }
			at AnnouncementDialog
	}
}
