package frontlinesms2

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactValidateBlankSaveSpec extends GebSpec {
	def 'trying to save with no name is invalid'() {
		given:
		when:
			to CreateContactPage
			saveButton.click()
		then:
			at CreateContactPage
			errorMessages.text().contains("cannot be blank")
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
