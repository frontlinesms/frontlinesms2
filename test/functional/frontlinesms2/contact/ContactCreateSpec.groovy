package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactCreateSpec extends ContactGebSpec {

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
