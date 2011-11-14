package frontlinesms2.announcement

import frontlinesms2.*

class AnnouncementCedSpec extends AnnouncementBaseSpec {
	
	def "can launch announcement screen from create new activity link" () {
		when:
			go "message"
			$("a", text:"Create new activity").click()
		then:
			waitFor { $("#activity-list").displayed }
		when:
			$("input", class: "announcement").click()
			$("#submit").click()
		then:
			waitFor { $("#ui-dialog-title-modalBox").text() == "New announcement" }
	}
	
	def "can create a new Activity" () {
		when:
			go "message"
			$("a", text:"Create new activity").click()
		then:
			waitFor { $("#activity-list").displayed }
		when:
			$("input", class: "announcement").click()
			$("#submit").click()
		then:
			waitFor { at AnnouncementDialog }
		when:
			$("#messageText").value("announcing this new announcement!")
			nextPageButton.click()
		then:
			waitFor { selectRecipientsTab.displayed }
		when:
			addressField.value("+919544426000")
			addAddressButton.click()
			nextPageButton.click()
		then:
			waitFor { confirmTab.displayed }
		when:
			addName.value("newbie")
			doneButton.click()
		then:
			waitFor { messagesQueuedNotification.displayed }
			Announcement.findByName("newbie").sentMessage == "announcing this new announcement!"
	}

	def 'existing announcements appear in activities section of messages'() {
			given:
				createTestAnnouncements()
			when:
				go 'message'
			then:
				$('#activities-submenu li')[0].text().contains('New Office')
				$('#activities-submenu li')[1].text().contains('Office Party')
	}
}
	
class AnnouncementDialog extends geb.Page {
	static at = {
		$("#ui-dialog-title-modalBox").text() == 'New announcement'
	}
	static content = {
		selectRecipientsTab { $('div#tabs-2') }
		confirmTab { $('div#tabs-3') }
		messagesQueuedNotification { $("div#tabs-4.summary") }
		
		addressField { $('#address') }
		addAddressButton { $('.add-address') }
		addName { $("#announcement-name") }
		
		nextPageButton { $("#nextPage") }
		doneButton { $("#submit") }
	}
}