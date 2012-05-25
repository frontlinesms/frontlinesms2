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
			frmDetails.contactname = 'Kate'
			frmDetails.contactmobile = '+2541234567'
			frmDetails.contactemail = 'gaga@gmail.com'
			$('#update-single').click()
		then:
			assertFieldDetailsCorrect('contactname', 'Name', 'Kate')
			assertFieldDetailsCorrect('contactmobile', 'Mobile', '+2541234567')
			changingContact.refresh()
			println Contact.findAll()*.name
			changingContact.name == 'Kate'
	}

	@spock.lang.IgnoreRest
	def "Updating a contact within a group keeps the view inside the group"() {
		given:
			def alice = Contact.findByName('Alice')
			Group g = new Group(name: 'Excellent').save(failOnError:true, flush:true)
			alice.addToGroups(g)
			alice.save(flush: true)
		when:
			to PageContactShowGroupContactAlice
			frmDetails.contactname = 'Kate'
			frmDetails.contactmobile = '+2541234567'
			frmDetails.contactemail = 'gaga@gmail.com'
			$('#update-single').click()
		then:
			at PageContactShowGroupContactAlice
			assertFieldDetailsCorrect('contactname', 'Name', 'Kate')
			Contact.findByName('Kate') != null
			assertFieldDetailsCorrect('contactname', 'Name', 'Kate')
			assertFieldDetailsCorrect('contactmobile', 'Mobile', '+2541234567')
			$('#groups-submenu .selected').text() == 'Excellent'
	}
	
	def "should remove address when delete icon is clicked"() {
		when:
			to PageContactShowBob
		then:
			$('#remove-contactmobile').displayed
			$("#contactmobile").siblings('a').displayed
		when:
			$('#remove-contactmobile').click()
		then:
			!$('#remove-contactmobile').displayed
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
			frmDetails.contactemail = 'bob@gmail.com'
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
			frmDetails.contactname = 'Kate'
			btnSave.click()
			$("#paging .currentStep").jquery.show();
		then:
			$("#paging .currentStep").text() == "2"
	}
	
}
