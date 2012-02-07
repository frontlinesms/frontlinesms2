package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import grails.plugin.geb.GebSpec

class ContactCreateSpec extends ContactBaseSpec {

	def 'ALL CONTACTS menu item is selected when creating a contact'() {
		when:
			to PageContactShow
		then:
			selectedMenuItem.text() == 'All contacts'
	}

	def 'button to create new contact exists and goes to NEW CONTACT page'() {
		when:
			to PageContactShow
		then:
			$("#create-contact a").@href == "/contact/createContact"
	}

	def 'button to create new group exists and goes to NEW GROUP page'() {
		when:
			to PageContactShow
		then:
			$("#create-group a").@href == "/group/create"
	}
	
}



