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
		given:
			def alice = Contact.findByName('Alice')
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${alice.id}"
			$("#contactDetails").name = 'Kate'
			$("#contactDetails").address = '+2541234567'
			def btn = $("#contactDetails .save")
			println btn
			btn.click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assert Contact.findByName('Kate') != null
			assertFieldDetailsCorrect('address', 'Address', '+2541234567')
	}

}
