package frontlinesms2.contact

import frontlinesms2.*

class ContactBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestContacts() {
		Contact.build(name:'Alice', mobile:'2541234567', notes:'notes')
		Contact.build(name:'Bob', mobile:'+254987654', email:"bob@bob.com")
	}
	
	static createTestMessages() {
		Fmessage.build(src:'Bob', dst:'MyNumber', text:'hi Bob')
		Fmessage.build(src:'Alice', dst:'MyNumber', text:'hi Alice')
	}

	static createTestGroups() {
		def bob = Contact.findByName('Bob')
		def groupThree = Group.build(name:'three')
		def groupTest = Group.build(name:'Test')
		['Others', 'four'].each { Group.build(name:it) }

		groupTest.addToMembers(bob)
		groupThree.addToMembers(bob)
		groupTest.save(failOnError:true, flush:true)
		groupThree.save(failOnError:true, flush:true)
	}

	static createTestCustomFields() {
		Contact.findByName('Bob')
				.addToCustomFields(name:'town', value:'Kusumu')
				.save(failOnError:true, flush:true)

		Contact.findByName('Alice')
				.addToCustomFields(name:'lake', value:'Victoria')
				.save(failOnError:true, flush:true)
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
			Contact.build(name:"Contact${it}", mobile:"987654321${it}", notes:'notes')
		}
	}
	
}
