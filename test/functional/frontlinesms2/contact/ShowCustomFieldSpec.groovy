package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver

class ShowCustomFieldSpec extends ContactGebSpec {
	def setup() {
		createTestContacts()
		createTestCustomFields()
	}

	def cleanup() {
		deleteTestContacts()
		deleteTestCustomFields()
	}

	def "'add new custom field' is shown in dropdown and redirects to create page"() {
		when:
			def bob = Contact.findByName("Bob")
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def fieldSelecter = $("#contact-details").find('select', name:'new-field-dropdown')
			def nonfields = fieldSelecter.children().collect() { it.text() }
		then:
			nonfields == ['Add more information...', 'Create custom field', 'lake', 'town']
	}

	def 'custom fields with no value for that contact are shown in dropdown'() {
		when:
			def bob = Contact.findByName("Bob")
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def fieldSelecter = $("#contact-details").find('select', name:'new-field-dropdown')
			def nonfields = fieldSelecter.children().collect() { it.text() }
		then:
			nonfields == ['Add more information...', 'Create custom field', 'lake', 'town']
	}

	def 'custom fields with value for that contact are shown in list of details'() {
		when:
			def bob = Contact.findByName("Bob")
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def list = $("#custom-field-list").children().children('label').collect() { it.text() }
		then:
			list == ['town']
	}

	def 'clicking an existing custom field in dropdown adds it to list with blank value'() {
		when:
			def bob = Contact.findByName("Bob")
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def list = $("#custom-field-list").children().children('label').collect() { it.text() }
			def fieldSelecter = $("#contact-details").find('select', name:'new-field-dropdown')
			fieldSelecter.value('lake')
			def nonfields = fieldSelecter.children().collect() { it.text() }
			def updatedList = $("#custom-field-list").children().children('label').collect() { it.text() }
		then:
			list == ['town']
			updatedList.sort() == ['lake', 'town']
			nonfields == ['Add more information...', 'Create custom field', 'lake', 'town']
	}

	def 'clicking "x" next to custom field in list removes it from visible list, but does not change database iff no other action is taken'() {
		when:
			def bob = Contact.findByName("Bob")
			bob.addToCustomFields(CustomField.findByName('lake')).save(failOnError: true, flush: true)
			def bobsDatabaseFields = bob.getCustomFields()
			def bobsFields = bobsDatabaseFields
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def lstFields = $("#custom-field-list")
			assert lstFields.children().children('label').size() == 2
			lstFields.find('a').first().click()
			bobsFields = bob.getCustomFields()
			def lstUpdatedFields = $("#custom-field-list")
		then:
			lstUpdatedFields.children().children('label').size() == 1
			lstUpdatedFields.children().children('label').text() == 'town'
			bobsFields == bobsDatabaseFields
	}

	def 'clicking "x" next to custom field in list then saving removes it from  database'() {
		when:
			def bob = Contact.findByName("Bob")
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def lstFields = $("#custom-field-list")
			lstFields.find('a').first().click()
			$("#contact-details .update").click()
		then:
			bob.getCustomFields() == null
	}

	def 'clicking save actually adds field to contact in database iff value is filled in'() {
		when:
			def bob = Contact.findByName("Bob")
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def fieldSelecter = $("#contact-details").find('select', name:'new-field-dropdown')
			fieldSelecter.value('lake')
			def inputField =  $("#contact-details ").find('input', name:'lake')
			inputField.value('erie')
			$("#contact-details .update").click()
		then:
			bob.refresh()
			println "Bob has fields: ${bob.customFields}"
			bob.customFields.name == ['lake', 'town']
	}

	def "clicking save doesn't add field to contact in database if there is a blank value for field"() {
		when:
			def bob = Contact.findByName("Bob")
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def fieldSelecter = $("#contact-details").find('select', name:'new-field-dropdown')
			fieldSelecter.value('lake')
			$("#contact-details .update").click()
		then:
			bob.refresh()
			bob.customFields.name == ['town']
	}
}

