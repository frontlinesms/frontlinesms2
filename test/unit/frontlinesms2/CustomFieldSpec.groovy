package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class CustomFieldSpec extends UnitSpec {
	def "Custom Field must have a name and a contact"() {
		when:
			CustomField namelessField = new CustomField()
			CustomField namedField = new CustomField(name: 'address')
			CustomField contactField = new CustomField(name:'town', contact: new Contact(address: "12345"))
			mockForConstraintsTests(CustomField, [namelessField, namedField, contactField])
		then:
			!namelessField.validate()
			!namedField.validate()
			contactField.validate()
	}

	def "Custom Field may have a value"() {
		setup:
			mockForConstraintsTests(CustomField)
		when:
			CustomField f = new CustomField(name:'town', contact: new Contact(address: "12345"))
		then:
			f.validate()

		when:
			f.value = 'thica'
		then:
			f.validate()
	}

	def "max Custom Field name length 255"(){
		setup:
			mockForConstraintsTests(CustomField)
		when:
			def CustomField field = new CustomField(name:'''\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef''')
		then:
			!field.validate()
	}

	def "max Custom Field value length 255"(){
		setup:
			mockForConstraintsTests(CustomField)
		when:
			def CustomField field = new CustomField(name:'what', value:'''\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef''')
		then:
			!field.validate()
	}
}

