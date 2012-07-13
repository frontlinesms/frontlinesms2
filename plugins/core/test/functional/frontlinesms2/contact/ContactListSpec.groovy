package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import grails.plugin.geb.GebSpec

class ContactListSpec extends ContactBaseSpec {
	def 'contacts list is displayed'() {
		given:
			createTestContacts()
		when:
			to PageContactAll
		then:	
			contactList.contacts == ['Alice', 'Bob']
	}

	def 'contacts list not shown when no contacts exist'() {
		when:
			to PageContactAll
		then:
			contactList.noContent == 'No contacts here!'
	}

	def 'ALL CONTACTS menu item is selected in default view'() {
		when:
			to PageContactAll
		then:
			bodyMenu.selectedMenuItem == 'all contacts'
	}
	
	def 'contacts list is paginated'() {
		given:
			createManyContacts()
		when:
			to PageContactAll
		then:
			def contactNames = contactList.contacts
			def expectedNames = (11..60).collect{"Contact${it}"}
			assert contactNames == expectedNames
	}
	
	def 'should be able to search contacts'() {
		given:
			def samAnderson = Contact.build(name:'Sam Anderson')
			def samJones = Contact.build(name:'SAm Jones')
			def samTina = Contact.build(name:'SaM Tina')
			def bob = Contact.build(name:'bob')
		when:
			to PageContactAll
			footer.searchContact.jquery.trigger('focus')
			footer.searchContact << "Sam"
		then:
			waitFor { contactList.contacts == ['Sam Anderson', 'SAm Jones', 'SaM Tina'] }
	}
	
	def 'should be able to search contacts within a group'() {
		given:
			def fpGroup = Group.build(name:"Friends")
			def samAnderson = Contact.build(name:'Sam Anderson')
			def samJones = Contact.build(name:'SAm Jones')
			def samTina = Contact.build(name:'SaM Tina')
			def bob = Contact.build(name:'Bob')

			samAnderson.addToGroups(fpGroup, true)
			samJones.addToGroups(fpGroup, true)
			bob.addToGroups(fpGroup, true)
		when:
			to PageContactAll, fpGroup
			footer.searchContact.jquery.trigger('focus')
			footer.searchContact << "Sam"
		then:
			waitFor {contactList.contacts == ['Sam Anderson', 'SAm Jones'] }
	}
	
	def "should remain on the same page when a contact is selected"() {
		given:
			createManyContacts()
		when:
			to PageContactAll
			footer.nextPage.click()
		then:
			!footer.prevPage.disabled
		when:
			contactList.selectContact 1
		then:
			!footer.prevPage.disabled
	}
}
