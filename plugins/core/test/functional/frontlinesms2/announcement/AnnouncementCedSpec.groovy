package frontlinesms2.announcement

import frontlinesms2.*

class AnnouncementCedSpec extends AnnouncementBaseSpec {
	
	def "can launch announcement screen from create new activity link" () {
		when:
			go "message"
			$("a", text:"Create new activity").click()
		then:
			waitFor { $("#new-activity-choices").displayed }
		when:
			$("input.announcement").click()
			$("#submit").click()
		then:
			waitFor { $("#ui-dialog-title-modalBox").text().equalsIgnoreCase("New announcement") }
	}
	
	def "can create a new Announcement" () {
		when:
			go "message"
			$("a", text:"Create new activity").click()
		then:
			waitFor { $("#new-activity-choices").displayed }
		when:
			$("input.announcement").click()
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
			waitFor { $("#ui-dialog-title-modalBox").text().equalsIgnoreCase("Announcement saved!") }
			Announcement.findByName("newbie").sentMessageText == "announcing this new announcement!"
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

	def "should display errors when announcement validation fails"() {
		given:
			def announcement = new Announcement(name: "newbie", messageText: "announcing this new announcement!", messages:[]).save(failOnError:true)
		when:
			go "message"
			$("a", text:"Create new activity").click()
		then:
			waitFor { $("#new-activity-choices").displayed }
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
			assert Announcement.count() == 1
			waitFor { errorMessage.displayed }
			at AnnouncementDialog
	}
}
	
class AnnouncementDialog extends geb.Page {
	static at = {
		$("#ui-dialog-title-modalBox").text().equalsIgnoreCase('New announcement')
	}
	static content = {
		selectRecipientsTab { $('div#tabs-2') }
		confirmTab { $('div#tabs-3') }
		
		addressField { $('#address') }
		addAddressButton { $('.add-address') }
		addName { $("#name") }
		
		nextPageButton { $("#nextPage") }
		doneButton { $("#submit") }
		errorMessage(required:false) { $('.error-panel') }
	}
}
