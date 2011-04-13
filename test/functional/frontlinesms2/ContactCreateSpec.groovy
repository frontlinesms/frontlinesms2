package frontlinesms2

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactCreateSpec extends ContactGebSpec {
	def cleanup() {
		deleteTestContacts()
	}

	def 'button to save new contact is displayed and works'() {
		given:
		when:
			to CreateContactPage
			$("#contactDetails").name = 'Kate'
			$("#contactDetails").address = '+2541234567'
			saveButton.click()
		then:
			at ContactListPage
	}

	def 'trying to save with no name is invalid'() {
		given:
		when:
			to CreateContactPage
			saveButton.click()
		then:
			at CreateContactPage
			errorMessages.text().contains("cannot be blank")
	}
	
	def 'link to cancel creating a new contact is displayed and goes back to main contact page'() {
		given:
		when:
			go 'contact/create'
			def cancelContact = $('#buttons').find('a').first()
			def btn = $("#buttons .list")
		then:
			assert cancelContact.text() == "Cancel"
			assert cancelContact.getAttribute('href') == "/frontlinesms2/contact/list"

	}
}

class CreateContactPage extends geb.Page {
	static url = 'contact/create'
	static at = {
		title.endsWith('Create Contact')
	}
	static content = {
		saveButton { $("#contactDetails .save") }
		errorMessages { $('.errors') }
	}
}

class ContactListPage extends geb.Page {
	static url = 'contact/list'
}
