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
			to PageContactShowAlice
			def changingContact = Contact.findByName('Alice')
			frmDetails.name = 'Kate'
			frmDetails.mobile = '+2541234567'
			frmDetails.email = 'gaga@gmail.com'
			$('#update-single').click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('mobile', 'Mobile (Primary)', '+2541234567')
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
			frmDetails.mobile = '+2541234567'
			frmDetails.email = 'gaga@gmail.com'
			$('#update-single').click()
		then:
			at PageContactShowGroupContactAlice
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			Contact.findByName('Kate') != null
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('mobile', 'Mobile (Primary)', '+2541234567')
			$('#groups-submenu .selected').text() == 'Excellent'
	}
	
	def "should remove address when delete icon is clicked"() {
		when:
			to PageContactShowBob
		then:
			$('#remove-mobile').displayed
			$("#mobile").siblings('a').displayed
		when:
			$('#remove-mobile').click()
		then:
			!$('#remove-mobile').displayed
			!$('.basic-info .send-message').displayed
	}
	
	def "should disable the save and cancel buttons when viewing a contact details"() {
		when:
			go "contact/show/${Contact.findByName('Bob').id}"
		then:
			at PageContactShowBob
			btnSave.disabled
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
			$("#paging .nextLink").click()
			$("#paging .currentStep").jquery.show();
		then:
			$("#paging .currentStep").text() == "2"
		when:
			frmDetails.name = 'Kate'
			btnSave.click()
			$("#paging .currentStep").jquery.show();
		then:
			$("#paging .currentStep").text() == "2"
	}
	
}
