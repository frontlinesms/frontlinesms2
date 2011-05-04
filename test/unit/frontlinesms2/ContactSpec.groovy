package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ContactSpec extends UnitSpec {
	def setup() {
		mockForConstraintsTests(Contact)
	}
	
	def "contact may have a name"() {
		when:
			Contact c = new Contact()
			assert c.name == null
			c.name = 'Alice'
		then:
			c.name == 'Alice'
	}

	def "blank names are allowed"() {
		when:
			def noNameContact = new Contact(name:'')
			def namedContact = new Contact(name:'Alice')
		then:
			noNameContact.validate()
			namedContact.validate()
	}
	
	def "duplicate names are allowed"(){
		when:
			def Contact contact1=new Contact(name:'John')
			def Contact contact2=new Contact(name:'John')
		then:
			contact1.validate()
			contact2.validate()
	}
	
	def "max name length 255"(){
		when:
			def Contact contact=new Contact(name:'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'0123456789'+
												'012345')
	then:
		!contact.validate()
	}
}

