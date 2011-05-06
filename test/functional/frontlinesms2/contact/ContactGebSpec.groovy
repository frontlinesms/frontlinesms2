package frontlinesms2.contact

import frontlinesms2.*

class ContactGebSpec extends grails.plugin.geb.GebSpec {

	static createTestContacts() {	
		[new Contact(name: 'Alice', address: '+2541234567'),
			new Contact(name: 'Bob', address: '+254987654')].each() { it.save(failOnError:true) }
	}

	static createTestGroups() {
		def bob = Contact.findByName('Bob')
		def groupThree = new Group(name: 'three')
		def groupTest = new Group(name: 'Test')
		[groupTest, new Group(name: 'Others'), groupThree, new Group(name: 'four')].each() {
			it.save(failOnError:true, flush:true)
		}
		groupTest.addToMembers(bob)
		groupThree.addToMembers(bob)
	}

	static deleteTestContacts() {
		Contact.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}

	static deleteTestGroups() {
		Group.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}


	def assertFieldDetailsCorrect(fieldName, labelText, expectedValue) {
		def label = $('label', for:fieldName)
		assert label.text() == labelText
		assert label.getAttribute('for') == fieldName

		def input = $('input', id:fieldName)
		assert input.getAttribute('name') == fieldName
		assert input.getAttribute('id') == fieldName
		assert input.getAttribute('type')  == 'text'
		assert input.getAttribute('value')  == expectedValue
		true
	}
}
