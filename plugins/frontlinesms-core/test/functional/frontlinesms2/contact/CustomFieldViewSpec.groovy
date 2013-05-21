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

	def 'clicking X next to custom field in list removes it from visible list, but does not change database if no other action is taken'() {
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
		then:
			singleContactDetails.contactsCustomFields.size() == 1
			singleContactDetails.contactsCustomFields == ['lake']
			bob.refresh().customFields.size() == 2
	}

	def 'clicking X next to custom field in list then saving removes it from  database'() {
		when:
			to PageContactShow, 'Bob'
			singleContactDetails.removeCustomFeild(remote { CustomField.findByName("town").id })
			singleContactDetails.save.click()
		then:
			waitFor { remote { !CustomField.findByContact(bob) } }
	}

	def 'clicking save actually adds field to contact in database if value is filled in'() {
		when:
			to PageContactShow, 'Bob'
			singleContactDetails.addCustomField 'lake'
			def customFeild = singleContactDetails.customField 'lake'
			customFeild.value('erie')
			singleContactDetails.save.click()
		then:
			singleContactDetails.contactsCustomFields == ['lake', 'town']
	}

	def "clicking save doesn't add field to contact in database if there is a blank value for field"() {
		when:
			to PageContactShow, 'Bob'
			singleContactDetails.addCustomField 'lake'
			singleContactDetails.save.click()
		then:
			remote { Contact.findByName('Bob').customFields*.name } == ['town']
	}
}

