package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class DeleteContactSpec extends ContactGebSpec {
	def 'delete button is displayed and works'() {
		given:
			createTestContacts()
		when:
			go "contact/show/${Contact.findByName('Alice').id}"
			def deleteBtn = $('.delete')
			withConfirm(true) { deleteBtn.click()}
		then:
			!Contact.findAllByName('Alice')
		cleanup:
			deleteTestContacts()
	}
}
