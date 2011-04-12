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
			def firstContactListItem = $('#contacts').children().first()
			println " firstContactListItem: ${firstContactListItem}"
			println " firstContactListItem.children(): ${firstContactListItem.children().collect() { it.tag() }}"
			def anchor = firstContactListItem.children('a').first()
			assert anchor.text() == 'Alice'
			assert anchor.getAttribute('href') == "/frontlinesms2/contact/show/${alice.id}"
	}


	def 'selected contact is highlighted'() {
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

	def 'selected contact details are displayed'() {
		given:
			def alice = Contact.findByName('Alice')
			def bob = Contact.findByName('Bob')
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${alice.id}"
		then:
			def contactDetails = $('#contactinfo')

			def contactName = contactDetails.children('div#name')
			assert contactName.getAttribute("id") == 'name'
			assert contactName.children('label').text() == 'Name'
			assert contactName.children('label').getAttribute('for') == 'name'
			assert contactName.children('input').getAttribute('name') == 'name'
			assert contactName.children('input').getAttribute('id') == 'name'
			assert contactName.children('input').getAttribute('type')  == 'text'
			assert contactName.children('input').getAttribute('value')  == 'Alice'

			def contactAddress = contactDetails.children('div#address')
			assert contactAddress.getAttribute("id") == 'address'
			assert contactAddress.children('label').text() == 'Address'
			assert contactAddress.children('label').getAttribute('for') == 'address'
			assert contactAddress.children('input').getAttribute('name') == 'address'
			assert contactAddress.children('input').getAttribute('id') == 'address'
			assert contactAddress.children('input').getAttribute('type')  == 'text'
			assert contactAddress.children('input').getAttribute('value')  == '+2541234567'
	}

	def assertContactSelected(String name) {
		def selectedChildren = $('#contacts').children('li.selected')
		assert selectedChildren.size() == 1
		assert selectedChildren.text() == name
		true
	}
}
