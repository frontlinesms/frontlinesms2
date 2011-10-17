package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactListSpec extends ContactBaseSpec {
	def 'contacts list is displayed'() {
		given:
			createTestContacts()
		when:
			go 'contact'
		then:	
			$('ol#contact-list').children()*.text() == ['Alice', 'Bob']
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
			def samAnderson = new Contact(name: 'Sam Anderson', primaryMobile: "1234567891").save(failOnError: true)
			def samJones = new Contact(name: 'SAm Jones', primaryMobile: "1234567892").save(failOnError: true)
			def samTina = new Contact(name: 'SaM Tina', primaryMobile: "1234567893").save(failOnError: true)
			def bob = new Contact(name: 'bob', primaryMobile: "99999").save(failOnError: true)
		when:
			to PageContactShow
			$("#contact-search").jquery.trigger('focus')
			$("#contact-search") << "Sam"
		then:
			waitFor { $('#contact-list li').children('a')*.text() == ['Sam Anderson', 'SAm Jones','SaM Tina'] }
	}
	
	def 'should be able to search contacts within a group'() {
		given:
			def fpGroup = new Group(name: "Friends").save(failOnError: true, flush: true)
			def samAnderson = new Contact(name: 'Sam Anderson', primaryMobile: "1234567891").save(failOnError: true)
			def samJones = new Contact(name: 'SAm Jones', primaryMobile: "1234567892").save(failOnError: true)
			def samTina = new Contact(name: 'SaM Tina', primaryMobile: "1234567893").save(failOnError: true)
			samAnderson.addToGroups(fpGroup, true)
			samJones.addToGroups(fpGroup,true)
			def bob = new Contact(name: 'Bob', primaryMobile: "1234567894").save(failOnError: true).addToGroups(fpGroup,true)
		when:
			go "group/show/${fpGroup.id}"
			$("#contact-search").jquery.trigger('focus')
			$("#contact-search") << "Sam"
		then:
			waitFor { $('#contact-list li').children('a')*.text() == ['Sam Anderson', 'SAm Jones'] }
	}
	
	def "should remain on the same page when a contact is selected"() {
		given:
			createManyContacts()
		when:
			to PageContactShow
			$("a.nextLink").click()
			$("#page-arrows .currentStep").jquery.show();
		then:
			$("#page-arrows .currentStep").text() == "2"
		when:
			$('#contact-list li').children('a')[1].click()
			$("#page-arrows .currentStep").jquery.show();
		then:
			$("#page-arrows .currentStep").text() == "2"
	}
	
}
