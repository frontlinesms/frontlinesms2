package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactEditSpec extends ContactGebSpec {
	def setup() {
		createTestContacts()
	}

	def cleanup() {
		deleteTestContacts()
	}

	def 'selected contact details can be edited and saved'() {
		when:
			to AliceDetailsPage
			frmDetails.name = 'Kate'
			frmDetails.address = '+2541234567'
			btnSave.click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('address', 'Address', '+2541234567')
			Contact.findByName('Kate') != null
	}

	def "Updating a contact within a group keeps the view inside the group"() {
		given:
			def alice = Contact.findByName('Alice')
			Group g = new Group(name: 'Excellent').save(failOnError:true, flush:true)
			GroupMembership.create(alice, g, true)
		when:
			go "/frontlinesms2/group/show/${g.id}/contact/show/${alice.id}"
			frmDetails.name = 'Kate'
			frmDetails.address = '+2541234567'
			btnSave.click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('address', 'Address', '+2541234567')
			Contact.findByName('Kate') != null
			$('#groups-submenu .selected').text() == 'Excellent'
	}
}

class AliceDetailsPage extends geb.Page {
	static def getUrl() {
		"contact/show/${Contact.findByName('Alice').id}"
	}

	static content = {
		frmDetails { $("#contact-details") }
		btnSave { frmDetails.find('.update') }
	}
}
