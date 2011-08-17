package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactCreateSpec extends ContactGebSpec {
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
		errorMessages { $('.flash.errors') }
	}
}
