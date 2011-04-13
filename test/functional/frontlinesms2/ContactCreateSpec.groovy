package frontlinesms2

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactCreateSpec extends grails.plugin.geb.GebSpec {
	def 'link to create new contact is displayed in the menu'() {
		given:
		when:
			go 'contact'
			def createContact = $('#create-menu').find('a').first()
			def btn = $("#create-menu .create")
		then:
			assert createContact.text() == "Contact"
			assert createContact.getAttribute('href') == "/frontlinesms2/contact/create"

	}
}
