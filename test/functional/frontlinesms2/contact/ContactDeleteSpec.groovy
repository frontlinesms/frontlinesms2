package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactDeleteSpec extends ContactBaseSpec {
	def 'delete button is displayed and works'() {
		given:
			createTestContacts()
		when:
			to PageContactShowAlice
			deleteSingleButton.click()
		then:
			waitFor { confirmDeleteButton.displayed }
		when:
			confirmDeleteButton.click()
		then:
			waitFor { flashMessage.displayed }
			!Contact.findByName('Alice')
	}
	
	def 'should delete multiple selected contacts'() {
		given:
			createTestContacts()
		when:
			to PageContactShowAlice
			contactSelect[1].click()
		then:
			waitFor { $('input', name:'name').value() == 'Bob' }
		when:
			contactSelect[0].click()
		then:
			waitFor { deleteAllButton.displayed }
		when:
			deleteAllButton.click()
		then:
			waitFor { confirmDeleteButton.displayed }
		when:
			confirmDeleteButton.click()
		then:
			waitFor { flashMessage.displayed }
			Contact.count() == 0
	}
}
