package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactCreateSpec extends ContactGebSpec {
	def cleanup() {
		deleteTestContacts()
	}

	def 'button to save new contact is displayed and works'() {
		when:
			to CreateContactPage
			$('#name').value('Kate')
			$('#address').value('987878778')
			saveButton.click()
		then:
			at ContactListPage
	}

	def 'trying to save with no name is valid'() {
		when:
			to CreateContactPage
			saveButton.click()
			$('#address').value('987878778')
		then:
			at ContactListPage
	}

	def 'link to cancel creating a new contact is displayed and goes back to main contact page'() {
		when:
			to CreateContactPage
			def cancelContact = $('.buttons .cancel')
		then:
			cancelContact.text() == "Cancel"
			at ContactListPage
	}

	def 'ALL CONTACTS menu item is selected when creating a contact'() {
		when:
			to ContactListPage
		then:
			selectedMenuItem.text() == 'All contacts'
	}
}

class CreateContactPage extends geb.Page {
	static url = 'contact/createContact'
	static at = {
		title.endsWith('Create Contact')
	}
	static content = {
		saveButton { $("#contact-details .save") }
		errorMessages { $('.flash.errors') }
	}
}
