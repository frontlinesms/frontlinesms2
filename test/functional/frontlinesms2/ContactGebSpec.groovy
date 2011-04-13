package frontlinesms2

class ContactGebSpec extends grails.plugin.geb.GebSpec {

	static createTestContacts() {	
		[new Contact(name: 'Alice', address: '+2541234567'),
			new Contact(name: 'Bob', address: '+254987654')].each() { it.save(failOnError:true) }
	}

	static deleteTestContacts() {
		Contact.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)		
		}
	}

	def assertFieldDetailsCorrect(fieldName, labelText, expectedValue) {
			def contactName = $('#contactinfo').children("div#${fieldName}")
			assert contactName.getAttribute("id") == "${fieldName}"
			assert contactName.children('label').text() == "${labelText}"
			assert contactName.children('label').getAttribute('for') == "${fieldName}"
			assert contactName.children('input').getAttribute('name') == "${fieldName}"
			assert contactName.children('input').getAttribute('id') == "${fieldName}"
			assert contactName.children('input').getAttribute('type')  == 'text'
			assert contactName.children('input').getAttribute('value')  == "${expectedValue}"
			true
	}
}
