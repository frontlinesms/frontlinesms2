package frontlinesms2

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactShowSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		ContactSpecUtils.createTestContacts()
	}

	def cleanup() {
		ContactSpecUtils.deleteTestContacts()
	}

	def 'contacts link to their details'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go 'contact'
			println $('body').text()
		then:
			def contactDetails = $('#contacts')

			def firstContactListItem = $('#contacts').children().first()
			println " firstContactListItem: ${firstContactListItem}"
			println " firstContactListItem.children(): ${firstContactListItem.children().collect() { it.tag() }}"
			def anchor = firstContactListItem.children('a').first()
			assert anchor.text() == 'Alice'
			assert anchor.getAttribute('href') == "/frontlinesms2/contact/show/${alice.id}"
	}


	def 'contacts details are displayed'() {
		given:
			def alice = Contact.findByName('Alice')
			def bob = Contact.findByName('Bob')
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${alice.id}"
		then:
			assertContactSelected('Alice')
		    
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
		then:
			assertContactSelected('Bob')
	}

	def assertContactSelected(String name) {
		def selectedChildren = $('#contacts').children('li.selected')
		assert selectedChildren.size() == 1
		assert selectedChildren.text() == name
		true
	}
}
