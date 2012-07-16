package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser

class CustomFieldViewSpec extends ContactBaseSpec {
	def setup() {
		createTestContacts()
		createTestCustomFields()
	}
	
	def "'add new custom field' is shown in dropdown and redirects to create page"() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactShow, bob
		then:
			singleContactDetails.customFields == ['na', 'lake', 'add-new']
	}

	def 'custom fields with no value for that contact are shown in dropdown'() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactShow, bob
		then:
			singleContactDetails.customFields == ['na', 'lake', 'add-new']
	}

	def 'custom fields with value for that contact are shown in list of details'() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactShow, bob
		then:
			singleContactDetails.contactsCustomFields == ['town']
	}

	def 'clicking an existing custom field in dropdown adds it to list with blank value'() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactShow, bob
		then:
			singleContactDetails.addCustomField 'lake'
			def customFeild = singleContactDetails.customField 'lake'
			customFeild.displayed
			customFeild.value() == ""
	}

	def 'clicking X next to custom field in list removes it from visible list, but does not change database if no other action is taken'() {
		given:
			def bob = Contact.findByName("Bob")
			bob.addToCustomFields(name:'lake', value: 'Erie').save(failOnError: true, flush: true)
		when:
			to PageContactShow, bob
			singleContactDetails.contactsCustomFields.size() == 2
			singleContactDetails.removeCustomFeild CustomField.findByName("town").id
		then:
			singleContactDetails.contactsCustomFields.size() == 1
			singleContactDetails.contactsCustomFields == ['lake']
			bob.refresh().customFields.size() == 2
	}

	def 'clicking X next to custom field in list then saving removes it from  database'() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactShow, bob
			singleContactDetails.removeCustomFeild CustomField.findByName("town").id
			singleContactDetails.save.click()
		then:
			waitFor { !CustomField.findByContact(bob) }
	}

	def 'clicking save actually adds field to contact in database if value is filled in'() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactShow, bob
			singleContactDetails.addCustomField 'lake'
			def customFeild = singleContactDetails.customField 'lake'
			customFeild.value('erie')
			singleContactDetails.save.click()
		then:
			singleContactDetails.contactsCustomFields == ['lake', 'town']
	}

	def "clicking save doesn't add field to contact in database if there is a blank value for field"() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactShow, bob
			singleContactDetails.addCustomField 'lake'
			singleContactDetails.save.click()
		then:
			bob.refresh()
			bob.customFields.name == ['town']
	}
}

