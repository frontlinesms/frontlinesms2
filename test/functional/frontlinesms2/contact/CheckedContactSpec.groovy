package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class CheckedContactSpec extends ContactGebSpec {
	
	def 'should update screen to show number of selected messages'() {
		given:
			createTestContacts()
		when:
			to ContactShowPage
			contactSelect[1].click()
		then:
			waitFor { $('input', name:'name').value() == 'Bob' }
		when:
			contactSelect[0].click()
		then:
			waitFor { contactCount.text() == '2 contacts selected' }
			contactSelect[0].@checked == "true"
			contactSelect[1].@checked == "true"
	}
}

class ContactShowPage extends geb.Page {
	static url = 'contact'
	static content = {
		contactSelect(required:false) { $(".contact-select") }	
		contactCount(required:false) { $('#contact-count') }
	}
}