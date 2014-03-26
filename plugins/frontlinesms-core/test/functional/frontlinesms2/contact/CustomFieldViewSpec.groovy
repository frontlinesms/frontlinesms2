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
			to PageContactShow, 'Bob'
		then:
			singleContactDetails.customFields == ['na', 'lake', 'add-new']
	}

	def 'custom fields with no value for that contact are shown in dropdown'() {
		when:
			to PageContactShow, 'Bob'
		then:
			singleContactDetails.customFields == ['na', 'lake', 'add-new']
	}

	def 'custom fields with value for that contact are shown in list of details'() {
		when:
			to PageContactShow, 'Bob'
		then:
			singleContactDetails.contactsCustomFields == ['town']
	}

	def 'clicking an existing custom field in dropdown adds it to list with blank value'() {
		when:
			to PageContactShow, 'Bob'
		then:
			singleContactDetails.addCustomField 'lake'
			def customFeild = singleContactDetails.customField 'lake'
			customFeild.displayed
			customFeild.value() == ""
	}

	def 'clicking X next to custom field in list removes it from visible list, and makes relevant changes to the database'() {
		given:
			remote {
				Contact.findByName("Bob")
						.addToCustomFields(name:'lake', value:'Erie')
						.save(failOnError:true, flush:true)
				null
			}
		when:
			to PageContactShow, 'Bob'
			singleContactDetails.contactsCustomFields.size() == 2
			singleContactDetails.removeCustomFeild(remote { CustomField.findByName("town").id })
			singleContactDetails.name.focus()
		then:
			singleContactDetails.contactsCustomFields.size() == 1
			singleContactDetails.contactsCustomFields == ['lake']
			Contact.findByName("Bob").customFields.size() == 1
	}

	def 'clicking X next to custom field in list then saving removes it from  database'() {
		when:
			to PageContactShow, 'Bob'
			singleContactDetails.removeCustomFeild(remote { CustomField.findByName("town").id })
			singleContactDetails.name.focus()
		then:
			waitFor { remote { !CustomField.findByContact(Contact.findByName("Bob")) } }
	}

	def "adding a custom field to a contact does not add it to the database if there is a blank value for field"() {
		when:
			to PageContactShow, 'Bob'
			singleContactDetails.addCustomField 'lake'
			singleContactDetails.name.focus()
		then:
			remote { Contact.findByName('Bob').customFields*.name } == ['town']
	}
}

