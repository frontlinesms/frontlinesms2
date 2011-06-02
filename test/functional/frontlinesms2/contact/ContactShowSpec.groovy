package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactShowSpec extends ContactGebSpec {
	def setup() {
		createTestContacts()
	}

	def cleanup() {
		deleteTestContacts()
	}

	def 'contacts link to their details'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go 'contact'
		then:
			def firstContactListItem = $('#contacts').children().first()
			def anchor = firstContactListItem.children('a').first()
			assert anchor.text() == 'Alice'
			assert anchor.getAttribute('href') == "/frontlinesms2/contact/show/${alice.id}"
	}

	def 'contact with no name can be clicked and edited because his address is displayed'() {
		when:
			def empty = new Contact(name:'', address:"+987654321")
			empty.save(failOnError:true)
			go "http://localhost:8080/frontlinesms2/contact/list"
			def noName = Contact.findByName('')
		then:
			noName != null
			$('a', href:"/frontlinesms2/contact/show/${noName.id}").text().trim() == noName.address
	}

	def 'contact with no name or address can be clicked and edited'() {
		when:
			def empty = new Contact(name:'', address:"")
			empty.save(failOnError:true)
			go "http://localhost:8080/frontlinesms2/contact/list"
			def noNameOrAddress = Contact.get(empty.id)
		then:
			noNameOrAddress != null
			$('a', href:"/frontlinesms2/contact/show/${noNameOrAddress.id}").text().trim().length() > 0
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
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${alice.id}"
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Alice')
			assertFieldDetailsCorrect('address', 'Address', '+2541234567')
	}

	def 'contact with no groups has NO GROUPS message visible'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${alice.id}"
		then:
			$('#no-groups').displayed
	}

	def 'contact with groups has NO GROUPS message hidden'() {
		given:
			createTestGroups()
			def bob = Contact.findByName('Bob')
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
		then:
			!$('#no-groups').displayed
		cleanup:
			deleteTestGroups()
	}

	def assertContactSelected(String name) {
		def selectedChildren = $('#contacts').children('li.selected')
		assert selectedChildren.size() == 1
		assert selectedChildren.text() == name
		true
	}
}

class EmptyContactPage extends geb.Page {
	static def getUrl() {
		"contact/show/${Contact.findByName('').id}"
	}

	static at = {
		assert url == "http://localhost:8080/frontlinesms2/contact/show/${Contact.findByName('').id}"
		true
	}

	static content = {
		frmDetails { $("#contact-details") }
		btnSave { frmDetails.find('.update') }
	}
}
