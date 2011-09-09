package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactEditSpec extends ContactGebSpec {
	def setup() {
		createTestContacts()
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
	
	def "should remove secondary mobile address when delete icon is clicked"() {
		when:
			go "contact/show/${Contact.findByName('Bob').id}"
			assert $('div.basic-info:nth-child(4) a', class: 'remove-field').displayed
			assert $('div.basic-info:nth-child(4) a', class: 'send-message').displayed
			$('div.basic-info:nth-child(4) a', class: 'remove-field').click()
		then:
			!$('div.basic-info:nth-child(4) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(4) a', class: 'send-message').displayed
		when:		
			btnSave.click()
		then:
			!$('div.basic-info:nth-child(4) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(4) a', class: 'send-message').displayed
			assertFieldDetailsCorrect('secondaryMobile', 'Other Mobile', '')
		when: 
			go "contact/show/${Contact.findByName('Bob').id}"
		then:
			!$('div.basic-info:nth-child(4) a', class: 'remove-field').displayed
	}
	
	def "should remove email data when delete icon is clicked"() {
		when:
			go "contact/show/${Contact.findByName('Bob').id}"
			assert $('div.basic-info:nth-child(5) a', class: 'remove-field').displayed
			assert $('div.basic-info:nth-child(5) a', class: 'quick_message').displayed
			$('div.basic-info:nth-child(5) a', class: 'remove-field').click()
		then:
			!$('div.basic-info:nth-child(5) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(5) a', class: 'send-message').displayed
		when:
			btnSave.click()
		then:
			!$('div.basic-info:nth-child(5) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(5) a', class: 'send-message').displayed
			assertFieldDetailsCorrect('email', 'Email', '')
		when: 
			go "contact/show/${Contact.findByName('Bob').id}"
		then:
			!$('div.basic-info:nth-child(5) a', class: 'remove-field').displayed
	}
	
	def "should remove primary mobile address when delete icon is clicked"() {
		when:
			go "contact/show/${Contact.findByName('Bob').id}"
			assert $('div.basic-info:nth-child(3) a', class: 'remove-field').displayed
			assert $('div.basic-info:nth-child(3) a', class: 'send-message').displayed
			$('div.basic-info:nth-child(3) a', class: 'remove-field').click()
		then:
			!$('div.basic-info:nth-child(3) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(3) a', class: 'send-message').displayed
		when:
			btnSave.click()
		then:
			!$('div.basic-info:nth-child(3) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(3) a', class: 'send-message').displayed
			assertFieldDetailsCorrect('primaryMobile', 'Mobile (Primary)', '')
		when: 
			go "contact/show/${Contact.findByName('Bob').id}"
		then:
			!$('div.basic-info:nth-child(3) a', class: 'remove-field').displayed
	}
}

class AliceDetailsPage extends geb.Page {
	static def getUrl() {
		"contact/show/${Contact.findByName('Alice').id}"
	}

	static content = {
		frmDetails { $("#contact_details") }
		btnSave { frmDetails.find('#update-single') }
	}
}
