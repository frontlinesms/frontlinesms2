package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import grails.plugin.geb.GebSpec

class ContactCreateSpec extends ContactBaseSpec {

	def 'ALL CONTACTS menu item is selected when creating a contact'() {
		when:
			to PageContactShow
		then:
			bodyMenu.selectedMenuItem == 'all contacts (0)'
	}

	def 'button to create new contact exists and goes to NEW CONTACT page'() {
		when:
			to PageContactShow
		then:
			bodyMenu.newContact.@href.contains "/contact/createContact"
	}

	def 'button to create new group exists and goes to NEW GROUP page'() {
		when:
			to PageContactShow
		then:
			bodyMenu.newGroup.@href.contains "/group/create"
	}
	
}



