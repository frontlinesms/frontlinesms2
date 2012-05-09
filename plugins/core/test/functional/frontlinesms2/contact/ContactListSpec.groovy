package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import grails.plugin.geb.GebSpec

class ContactListSpec extends ContactBaseSpec {
	def 'contacts list is displayed'() {
		given:
			createTestContacts()
		when:
			go 'contact'
		then:	
			$('#contact-list').children()*.text() == ['Alice', 'Bob']
	}

	def 'contacts list not shown when no contacts exist'() {
		when:
			go 'contact'
		then:
			$('div#contact-list').text() == 'No contacts here!'
	}

	def 'ALL CONTACTS menu item is selected in default view'() {
		when:
			to PageContactShow
		then:
			selectedMenuItem.text() == 'All contacts'
	}
	
	def 'contacts list is paginated'() {
		given:
			createManyContacts()
		when:
			to PageContactShow
		then:
			def contactList = $('#contact-list')
			def contactNames = contactList.children()*.text()
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
			to PageContactShow
			$("#contact-search").jquery.trigger('focus')
			$("#contact-search") << "Sam"
		then:
			waitFor { $('#contact-list li a')*.text() == ['Sam Anderson', 'SAm Jones', 'SaM Tina'] }
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
			go "group/show/${fpGroup.id}"
			$("#contact-search").jquery.trigger('focus')
			$("#contact-search") << "Sam"
		then:
			waitFor {
println $('#contact-list li').children('a')*.text()
 $('#contact-list li').children('a')*.text() == ['Sam Anderson', 'SAm Jones'] }
	}
	
	def "should remain on the same page when a contact is selected"() {
		given:
			createManyContacts()
		when:
			go 'contact/show'
			$("#paging .nextLink").click()
			$("#paging .currentStep").jquery.show();
		then:
			at PageContactShow
			$("#paging .currentStep").text() == "2"
		when:
			$('#contact-list li').children('a')[1].click()
			$("#paging .currentStep").jquery.show();
		then:
			$("#paging .currentStep").text() == "2"
	}
	
}
