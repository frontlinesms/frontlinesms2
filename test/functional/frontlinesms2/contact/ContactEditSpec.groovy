package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactEditSpec extends ContactGebSpec {
	def setup() {
		createTestContacts()
	}

	def cleanup() {
		deleteTestContacts()
	}

	def 'selected contact details can be edited and saved'() {
		when:
			to AliceDetailsPage
			frmDetails.name = 'Kate'
			frmDetails.address = '+2541234567'
			btnSave.click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('address', 'Address', '+2541234567')
			Contact.findByName('Kate') != null
	}
}

class AliceDetailsPage extends geb.Page {
	static def getUrl() {
		"contact/show/${Contact.findByName('Alice').id}"
	}

	static content = {
		frmDetails { $("#contact-details") }
		btnSave { frmDetails.find('.update') }
	}
}
