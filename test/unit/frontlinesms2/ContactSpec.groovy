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

	def "blank names are allowed, there is no minimum length for name"() {
		when:
			def noNameContact = new Contact(name:'')
			def namedContact = new Contact(name:'a')
		then:
			noNameContact.validate()
			namedContact.validate()
	}
	
	def "duplicate names are allowed"(){
		setup:
			mockDomain(Contact)
		when:
			def Contact contact1 = new Contact(name:'John')
			def Contact contact2 = new Contact(name:'John')
		then:
			contact1.save()
			contact2.save()
	}
	
	def "max name length 255"(){
		when:
			def Contact contact = new Contact(name:
					'0123456789abcdef'+'0123456789abcdef'+'0123456789abcdef'+'0123456789abcdef'+
					'0123456789abcdef'+'0123456789abcdef'+'0123456789abcdef'+'0123456789abcdef'+
					'0123456789abcdef'+'0123456789abcdef'+'0123456789abcdef'+'0123456789abcdef'+
					'0123456789abcdef'+'0123456789abcdef'+'0123456789abcdef'+'0123456789abcdef')
	then:
		!contact.validate()
	}
}

