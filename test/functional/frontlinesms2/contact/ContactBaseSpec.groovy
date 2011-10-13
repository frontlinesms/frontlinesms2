package frontlinesms2.contact

import frontlinesms2.*

class ContactBaseSpec extends grails.plugin.geb.GebSpec {

	static createTestContacts() {
		new Contact(name: 'Alice', primaryMobile: '2541234567', notes: 'notes').save(failOnError:true, flush: true)
		new Contact(name: 'Bob', primaryMobile: '+254987654', secondaryMobile: "+232345675", email: "bob@bob.com").save(failOnError:true, flush: true)
	}
	
	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'MyNumber', text:'hi Bob'),
			new Fmessage(src:'Alice', dst:'MyNumber', text:'hi Alice')].each() {
				it.status = MessageStatus.INBOUND
				it.save(failOnError:true)
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
		assert label.getAttribute('for') == fieldName
		def input = $("#$fieldName")
		assert input.getAttribute('name') == fieldName
		assert input.getAttribute('id') == fieldName
		assert input.getAttribute('value')  == expectedValue
		true
	}
	
	def createManyContacts() {
		(11..90).each {
			new Contact(name: "Contact${it}", primaryMobile: "987654321${it}", notes: 'notes').save(failOnError:true)
		}
	}
	
}
