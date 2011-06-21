package frontlinesms2.contact

import frontlinesms2.*

class CustomFieldCreateSpec extends ContactGebSpec {
	def setup() {
		createTestContacts()
		createTestCustomFields()
	}

	def cleanup() {
		deleteTestContacts()
		deleteTestCustomFields()
	}

	def 'button to save new custom field is displayed and works'() {
		when:
			to CreateCustomFieldPage
			def initNumFields = contactInstance.customFields.count()
			$("#field-details").name = 'Friends'
			$("#field-details").value = 'Joan'
			btnSave.click()
		then:
			assert contactInstance.customFields.count() == (initNumGroups + 1)
	}

	def 'link to cancel creating a new group is displayed and goes back to main contact page'() {
		when:
			to CreateCustomFieldPage
			def cancelField = $('#buttons').find('a').first()
			def btn = $("#buttons .list")
		then:
			assert cancelField.text() == "Cancel"
			assert cancelField.getAttribute('href') == "/frontlinesms2/contact/show/${contactInstance.id}"
	}

	def 'Errors are displayed when group fails to save'() {
		when:
			to CreateCustomFieldPage
			btnSave.click()
		then:
			errorMessages.present
	}
}

class CreateCustomFieldPage extends geb.Page {
	static url = 'contact/createCustomField'
	static at = {
		title.endsWith('Create Custom Field')
	}

	static content = {
		btnSave { $("#field-details .save") }
		errorMessages { $('.flash.message') }
	}
}
