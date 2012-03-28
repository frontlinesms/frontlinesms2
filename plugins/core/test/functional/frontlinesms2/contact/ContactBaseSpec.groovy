package frontlinesms2.contact

import frontlinesms2.*

class ContactBaseSpec extends grails.plugin.geb.GebSpec {

	static createTestContacts() {
		new Contact(name: 'Alice', primaryMobile: '2541234567', notes: 'notes').save(failOnError:true, flush: true)
		new Contact(name: 'Bob', primaryMobile: '+254987654', email: "bob@bob.com").save(failOnError:true, flush: true)
	}
	
	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'MyNumber', text:'hi Bob'),
			new Fmessage(src:'Alice', dst:'MyNumber', text:'hi Alice')].each() {
				it.inbound = true
				it.save(failOnError:true, flush: true)
			}
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

	def assertFieldDetailsCorrect(fieldName, labelText, expectedValue) {
		def label = $('label', for:fieldName)
		assert label.text() == labelText
		assert label.@for == fieldName
		def input = $("#$fieldName")
		assert input.@name == fieldName
		assert input.@id == fieldName
		assert input.@value  == expectedValue
		true
	}
	
	def createManyContacts() {
		(11..90).each {
			new Contact(name: "Contact${it}", primaryMobile: "987654321${it}", notes: 'notes').save(failOnError:true)
		}
	}
	
}
