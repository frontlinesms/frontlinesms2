package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ContactSpec extends UnitSpec {
	def "contact may have a name"() {
		setup:
			mockForConstraintsTests(Contact)
		when:
			Contact c = new Contact()
			assert c.name == null
			c.name = 'Alice'
		then:
			c.name == 'Alice'
	}

	def "blank names are allowed, there is no minimum length for name"() {
		setup:
			mockForConstraintsTests(Contact)
		when:
			def noNameContact = new Contact(name:'', address:'9876543')
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
		setup:
			mockForConstraintsTests(Contact)
		when:
			def Contact contact = new Contact(name:'''\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef''')
		then:
			!contact.validate()
	}

	def 'contact may have a custom field'() {
		setup:
			mockDomain(Contact)
		when:
			Contact c = new Contact(name: 'Eve')
			c.addToCustomfields(new CustomField())
		then:
			c.validate()
	}

	def 'contact may have multiple custom fields'() {
		setup:
			mockDomain(Contact)
		when:
			Contact c = new Contact(name: 'Eve')
			c.addToCustomfields(new CustomField())
			c.addToCustomfields(new CustomField())
		then:
			c.validate()
	}
}

