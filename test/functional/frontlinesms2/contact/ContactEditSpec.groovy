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
			frmDetails.primaryMobile = '+2541234567'
			frmDetails.secondaryMobile = '+2542334567'
			frmDetails.email = 'gaga@gmail.com'
			btnSave.click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('primaryMobile', 'Mobile (Primary)', '+2541234567')
			assertFieldDetailsCorrect('secondaryMobile', 'Other Mobile', '+2542334567')
			Contact.findByName('Kate') != null
	}

	def "Updating a contact within a group keeps the view inside the group"() {
		given:
			def alice = Contact.findByName('Alice')
			Group g = new Group(name: 'Excellent').save(failOnError:true, flush:true)
			alice.addToGroups(g)
		    alice.save(flush: true)
		when:
			go "/frontlinesms2/group/show/${g.id}/contact/show/${alice.id}"
			frmDetails.name = 'Kate'
			frmDetails.primaryMobile = '+2541234567'
			frmDetails.secondaryMobile = '+2542334567'
			frmDetails.email = 'gaga@gmail.com'
			btnSave.click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			Contact.findByName('Kate') != null
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('primaryMobile', 'Mobile (Primary)', '+2541234567')
			assertFieldDetailsCorrect('secondaryMobile', 'Other Mobile', '+2542334567')
			$('#groups-submenu .selected').text() == 'Excellent'
	  	cleanup:
	  		g.delete()
	}
	
	 def "'send Message' link should not displayed for invalid email address"() {
		when:
			to AliceDetailsPage
			frmDetails.primaryMobile = ''
	  		frmDetails.email = 'gagasaas'
			btnSave.click()
		then:
			$(".quick_message")*.text() == []
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
