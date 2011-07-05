package frontlinesms2.contact

import frontlinesms2.*

class ContactGebSpec extends grails.plugin.geb.GebSpec {

	static createTestContacts() {	
		[new Contact(name: 'Alice', primaryMobile: '+2541234567', notes: 'notes'),
			new Contact(name: 'Bob', primaryMobile: '+254987654', secondaryMobile: "+232345675", email: "bob@bob.com")].each() { it.save(failOnError:true) }
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
		groupTest.save(failOnError:true, flush:true)
		groupThree.save(failOnError:true, flush:true)
	}

	static createTestCustomFields() {
		def bob = Contact.findByName('Bob')
		def alice = Contact.findByName('Alice')
		[new CustomField(name: 'lake', value: 'Victoria', contact: alice),
				new CustomField(name: 'town', value: 'Kusumu', contact: bob)].each() {
					it.save(failOnError:true, flush:true)
				}
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

	static deleteTestCustomFields() {
		CustomField.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}

	def assertFieldDetailsCorrect(fieldName, labelText, expectedValue) {
		def label = $('label', for:fieldName)
		assert label.text() == labelText
		assert label.getAttribute('for') == fieldName

		def input = $("#$fieldName")
		assert input.getAttribute('name') == fieldName
		assert input.getAttribute('id') == fieldName
		assert input.getAttribute('value')  == expectedValue
		true
	}
}
