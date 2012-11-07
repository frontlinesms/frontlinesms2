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
			to PageContactShow, Contact.findByName('Alice')
			def changingContact = Contact.findByName('Alice')

			singleContactDetails.name.value('Kate')
			singleContactDetails.mobile.value('+2541234567')
			singleContactDetails.email.value('gaga@gmail.com')
			singleContactDetails.save.click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('mobile', 'Mobile', '+2541234567')
			changingContact.refresh()
			changingContact.name == 'Kate'
	}

	def "Updating a contact within a group keeps the view inside the group"() {
		given:
			def alice = Contact.findByName('Alice')
			Group g = new Group(name: 'Excellent').save(failOnError:true, flush:true)
			alice.addToGroups(g)
			alice.save(flush:true)
		when:
			to PageContactShow, g, Contact.findByName('Alice')
			singleContactDetails.name.value('Kate')
			singleContactDetails.mobile.value('+2541234567') 
			singleContactDetails.email.value('gaga@gmail.com')
			singleContactDetails.save.click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			Contact.findByName('Kate') != null
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('mobile', 'Mobile', '+2541234567')
			bodyMenu.selectedMenuItem == 'excellent'
	}
	
	def "should remove address when delete icon is clicked"() {
		when:
			to PageContactShow, Contact.findByName('Bob')
		then:
			singleContactDetails.removeMobile.displayed
		when:
			singleContactDetails.removeMobile.click()
		then:
			!singleContactDetails.removeMobile.displayed
			!singleContactDetails.sendMessage.displayed
	}
	
	def "should disable the save and cancel buttons when viewing a contact details"() {
		when:
			to PageContactShow, Contact.findByName('Bob')
		then:
			singleContactDetails.save.disabled
	}
	
	def "should enable save and cancel buttons when contact details are edited"() {
		when:
			to PageContactShow, Contact.findByName('Bob')
			singleContactDetails.email.value('bob@gmail.com')
		then:
			!singleContactDetails.save.disabled
			!singleContactDetails.cancel.disabled
	}
	
	def "should remain on the same page after updating a contact"() {
		given:
			createManyContacts()
		when:
			to PageContactShow, Contact.findByName('Bob')
			footer.nextPage.click()
		then:
			!footer.prevPage.disabled
		when:
			singleContactDetails.name = 'Kate'
			singleContactDetails.save.click()
		then:
			!footer.prevPage.disabled
	}
}
