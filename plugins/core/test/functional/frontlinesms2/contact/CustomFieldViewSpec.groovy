package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser

class CustomFieldViewSpec extends ContactBaseSpec {
	def setup() {
		createTestContacts()
		createTestCustomFields()
	}

	def "'add new custom field' is shown in dropdown and redirects to create page"() {
		when:
			def bob = Contact.findByName("Bob")
			go "contact/show/${bob.id}"
		then:
			at PageContactShowBob
		then:
			fieldSelecter.children('option')*.value() == ['na', 'Street address', 'City', 'Postcode', 'State', 'lake', 'town', 'add-new']
	}

	def 'custom fields with no value for that contact are shown in dropdown'() {
		when:
			def bob = Contact.findByName("Bob")
			go "contact/show/${bob.id}"
		then:
			at PageContactShowBob
		then:
			fieldSelecter.children('option')*.value() == ['na', 'Street address', 'City', 'Postcode', 'State', 'lake', 'town', 'add-new']
	}

	def 'custom fields with value for that contact are shown in list of details'() {
		when:
			def bob = Contact.findByName("Bob")
			go "contact/show/${bob.id}"
		then:
			$("#custom-field-list").children().children('label')*.text() == ['town']
	}

	@spock.lang.IgnoreRest
	def 'clicking an existing custom field in dropdown adds it to list with blank value'() {
		when:
			def bob = Contact.findByName("Bob")
			to PageContactShowBob
		then:
			$("#custom-field-list").children().children('label')*.text() == ['town']
			waitFor { fieldSelecter.displayed }
		when:
			fieldSelecter.value('lake').click()
		then:
			$("#custom-field-list").find('label')*.text().sort() == ['lake', 'town']
			fieldSelecter.children()*.text() == ['Add more information...', 'Street address', 'City', 'Postcode', 'State', 'lake', 'town', 'Create new...']
	}

	def 'clicking X next to custom field in list removes it from visible list, but does not change database iff no other action is taken'() {
		when:
			def bob = Contact.findByName("Bob")
			bob.addToCustomFields(CustomField.findByName('lake')).save(failOnError: true, flush: true)
			def bobsDatabaseFields = bob.getCustomFields()
			def bobsFields = bobsDatabaseFields
			go "contact/show/${bob.id}"
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

	def 'clicking X next to custom field in list then saving removes it from  database'() {
		when:
			def bob = Contact.findByName("Bob")
			go "contact/show/${bob.id}"
			def lstFields = $("#custom-field-list")
			lstFields.find('a').first().click()
			$("#contact-editor #update-single").click()
		then:
			bob.getCustomFields() == null
	}

	def 'clicking save actually adds field to contact in database iff value is filled in'() {
		when:
			def bob = Contact.findByName("Bob")
			go "contact/show/${bob.id}"
		then:
			at PageContactShowBob
		when:
			fieldSelecter.value('lake').click()
			def inputField =  $("#contact-editor").find('input', name:'lake')
			inputField.value('erie')
			$("#contact-editor #update-single").click()
			go "contact/show/${bob.id}"
			def updatedList = $("#custom-field-list").children().children('label').collect() { it.text() }
		then:
			updatedList == ['lake', 'town']
	}

	def "clicking save doesn't add field to contact in database if there is a blank value for field"() {
		when:
			def bob = Contact.findByName("Bob")
			go "contact/show/${bob.id}"
		then:
			at PageContactShowBob
		when:
			fieldSelecter.value('lake')
			$("#contact-details #update-single").click()
		then:
			bob.refresh()
			bob.customFields.name == ['town']
	}
}

