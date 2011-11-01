package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import grails.plugin.geb.GebSpec

class ContactEditSpec extends ContactBaseSpec {
	def setup() {
		createTestContacts()
	}

	
	def 'selected contact details can be edited and saved'() {
		when:
			go "contact/show/${Contact.findByName('Alice').id}"
		then:
			at PageContactShowAlice
		when:
			def changingContact = Contact.findByName('Alice')
			frmDetails.name = 'Kate'
			frmDetails.primaryMobile = '+2541234567'
			frmDetails.secondaryMobile = '+2542334567'
			frmDetails.email = 'gaga@gmail.com'
			$('#update-single').click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('primaryMobile', 'Mobile (Primary)', '+2541234567')
			assertFieldDetailsCorrect('secondaryMobile', 'Other Mobile', '+2542334567')
			sleep 5000
			changingContact.refresh()
			println Contact.findAll()*.name
			changingContact.name == 'Kate'
	}

	def "Updating a contact within a group keeps the view inside the group"() {
		given:
			def alice = Contact.findByName('Alice')
			Group g = new Group(name: 'Excellent').save(failOnError:true, flush:true)
			alice.addToGroups(g)
			alice.save(flush: true)
		when:
			to PageContactShowGroupContactAlice
			frmDetails.name = 'Kate'
			frmDetails.primaryMobile = '+2541234567'
			frmDetails.secondaryMobile = '+2542334567'
			frmDetails.email = 'gaga@gmail.com'
			$('#update-single').click()
		then:
			at PageContactShowGroupContactAlice
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			Contact.findByName('Kate') != null
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('primaryMobile', 'Mobile (Primary)', '+2541234567')
			assertFieldDetailsCorrect('secondaryMobile', 'Other Mobile', '+2542334567')
			$('#groups-submenu .selected').text() == 'Excellent'
	}
	
	def "should remove secondary mobile address when delete icon is clicked"() {
		when:
			to PageContactShowBob
			$('div.basic-info:nth-child(4) a', class: 'remove-field').click()
		then:
			at PageContactShowBob
			!$('div.basic-info:nth-child(4) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(4) a', class: 'send-message').displayed
		when:		
			$('#update-single').click()
		then:
			at PageContactShowBob
			!$('div.basic-info:nth-child(4) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(4) a', class: 'send-message').displayed
			assertFieldDetailsCorrect('secondaryMobile', 'Other Mobile', '')
		when: 
			to PageContactShowBob
		then:
			!$('div.basic-info:nth-child(4) a', class: 'remove-field').displayed
	}
	
	def "should remove email data when delete icon is clicked"() {
		when:
			to PageContactShowBob
			$('div.basic-info:nth-child(5) a', class: 'remove-field').click()
		then:
			at PageContactShowBob
			!$('div.basic-info:nth-child(5) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(5) a', class: 'send-message').displayed
		when:
			$("#single-contact #update-single").click()
		then:
			at PageContactShowBob
			!$('div.basic-info:nth-child(5) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(5) a', class: 'send-message').displayed
			assertFieldDetailsCorrect('email', 'Email', '')
		when: 
			to PageContactShowBob
		then:
			!$('div.basic-info:nth-child(5) a', class: 'remove-field').displayed
	}
	
	def "should remove primary mobile address when delete icon is clicked"() {
		when:
			to PageContactShowBob
			$('div.basic-info:nth-child(3) a', class: 'remove-field').click()
		then:
			at PageContactShowBob
			!$('div.basic-info:nth-child(3) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(3) a', class: 'send-message').displayed
		when:
			$("#single-contact #update-single").click()
		then:
			!$('div.basic-info:nth-child(3) a', class: 'remove-field').displayed
			!$('div.basic-info:nth-child(3) a', class: 'send-message').displayed
			assertFieldDetailsCorrect('primaryMobile', 'Mobile (Primary)', '')
		when: 
			to PageContactShowBob
		then:
			!$('div.basic-info:nth-child(3) a', class: 'remove-field').displayed
	}
	
	def "should disable the save and cancel buttons when viewing a contact details"() {
		when:
			go "contact/show/${Contact.findByName('Bob').id}"
		then:
			at PageContactShowBob
			btnSave.disabled
			btnCancel.disabled
	}
	
	def "should enable save and cancel buttons when contact details are edited"() {
		when:
			go "contact/show/${Contact.findByName('Bob').id}"
		then:
			at PageContactShowBob
		when:
			frmDetails.email = 'bob@gmail.com'
		then:
			!btnSave.disabled
			!btnCancel.disabled
	}
	
	def "should remain on the same page after updating a contact"() {
		given:
			createManyContacts()
		when:
			to PageContactShowBob
			$("a.nextLink").click()
			$("#page-arrows .currentStep").jquery.show();
		then:
			$("#page-arrows .currentStep").text() == "2"
		when:
			frmDetails.name = 'Kate'
			btnSave.click()
			$("#page-arrows .currentStep").jquery.show();
		then:
			$("#page-arrows .currentStep").text() == "2"
	}
	
}
