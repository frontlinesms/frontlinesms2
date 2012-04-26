package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(CustomField)
class CustomFieldSpec extends Specification {

	@Unroll
	def "Custom Field validation"() {
		given:
			def customField = new CustomField(name:name, value:value)
		when:
			def val = customField.validate()
		then:
			val == valid
		where:
			name  | value       | valid
			null  | null        | false
			null  | "testdata"  | false
			""    | "testdata"  | false
			"key" | "testdata"  | true
			"key" | null        | false
			"key" | ''          | true
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

