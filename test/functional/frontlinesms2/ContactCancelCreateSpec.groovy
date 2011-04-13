package frontlinesms2

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactCancelCreateSpec extends grails.plugin.geb.GebSpec {
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
