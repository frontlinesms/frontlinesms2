package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
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
			def btnCreateContact = $("#create-contact").find('a')
		then:
			assert btnCreateContact.getAttribute('href') == "/frontlinesms2/contact/createContact"
	}

	def 'button to create new group exists and goes to NEW GROUP page'() {
		when:
			to PageContactShow
			def btnCreateGroup = $("#create-group").find('a')
		then:
			assert btnCreateGroup.getAttribute('href') == "/frontlinesms2/group/create"
	}
	
}



