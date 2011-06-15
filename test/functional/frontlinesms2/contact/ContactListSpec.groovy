package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactListSpec extends ContactGebSpec {
	def 'contacts list is displayed'() {
		given:
			createTestContacts()
		when:
			go 'contact'
		then:
			def contactList = $('#contacts')
			assert contactList.tag() == 'ol'
			
			def contactNames = contactList.children().collect() {
				it.text()
			}
			assert contactNames == ['Alice', 'Bob']
		cleanup:
			deleteTestContacts()
	}

	def 'contacts list not shown when no contacts exist'() {
		when:
			go 'http://localhost:8080/frontlinesms2/contact'
		then:
			def c = $('#contacts')
			assert c.tag() == "div"
			assert c.text() == 'You have no contacts saved'
	}

	def 'ALL CONTACTS menu item is selected in default view'() {
		when:
			to ContactListPage
		then:
			selectedMenuItem.text() == 'All contacts'
	}
}
