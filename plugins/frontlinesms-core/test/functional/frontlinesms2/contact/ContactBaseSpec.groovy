package frontlinesms2.contact

import frontlinesms2.*

class ContactBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestContacts() {
		remote {
			Contact.build(name:'Alice', mobile:'+2541234567', notes:'notes')
			Contact.build(name:'Bob', mobile:'+254987654', email:"bob@bob.com")
			null
		}
	}
	
	static createTestMessages() {
		remote {
			TextMessage.build(src:'Bob', dst:'MyNumber', text:'hi Bob')
			TextMessage.build(src:'Alice', dst:'MyNumber', text:'hi Alice')
			null
		}
	}

	static createTestGroups() {
		remote {
			def bob = Contact.findByName('Bob')
			def groupThree = Group.build(name:'three')
			def groupTest = Group.build(name:'Test')
			['Others', 'four'].each { Group.build(name:it) }

			groupTest.addToMembers(bob)
			groupThree.addToMembers(bob)
			groupTest.save(failOnError:true, flush:true)
			groupThree.save(failOnError:true, flush:true)
			bob.save(failOnError:true, flush:true)
			null
		}
	}

	static createTestCustomFields() {
		remote {
			Contact.findByName('Bob')
					.addToCustomFields(name:'town', value:'Kusumu')
					.save(failOnError:true, flush:true)

			Contact.findByName('Alice')
					.addToCustomFields(name:'lake', value:'Victoria')
					.save(failOnError:true, flush:true)
			null
		}
	}

	def assertFieldDetailsCorrect(fieldName, labelText, expectedValue) {
		def label = singleContactDetails.labels fieldName
		assert label.@for == fieldName
		def input = singleContactDetails.textField fieldName
		assert input.@name == fieldName
		assert input.@id == fieldName
		assert input.@value  == expectedValue
		true
	}

	static createManyContacts() {
		remote {
			(11..90).each {
				Contact.build(name:"Contact${it}", mobile:"987654321${it}", notes:'notes')
			}
			null
		}
	}	
}
