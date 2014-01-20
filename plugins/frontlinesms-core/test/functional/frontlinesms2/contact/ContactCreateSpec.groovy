package frontlinesms2.contact

import frontlinesms2.*
import frontlinesms2.popup.*
import geb.Browser
import grails.plugin.geb.GebSpec

class ContactCreateSpec extends ContactBaseSpec {

	def 'ALL CONTACTS menu item is selected when creating a contact'() {
		when:
			to PageContactShow
		then:
			bodyMenu.selectedMenuItem == 'contact.all.contacts[0]'
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

	def 'form should fail validation if no name or number is provided'() {
		when:
			to PageContactCreate
			singleContactDetails.save.click()
		then:
			singleContactDetails.labels("name").collect{ it.text() }.contains("contact.name.validator.invalid")
			singleContactDetails.labels("mobile").collect{ it.text() }.contains("contact.name.validator.invalid")
	}

	def 'form should pass validation and save contact if valid values are provided'() {
		when:
			to PageContactCreate
			singleContactDetails.name = "Bob"
			singleContactDetails.mobile = "123456789"
			singleContactDetails.save.click()
		then:
			remote { Contact.findByNameAndMobile("Bob", "123456789").id }
	}

	def 'Import contacts should launch import wizard'() {
		when:
			to PageContactCreate
			bodyMenu.importContacts.click()
		then:
			waitFor { at ImportContactDialog }
	}

}



