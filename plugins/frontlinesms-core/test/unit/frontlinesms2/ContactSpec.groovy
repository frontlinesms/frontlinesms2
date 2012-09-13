package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(Contact)
class ContactSpec extends Specification {
	@Unroll
	def "test contact name and mobile validation"() {
		expect:
			new Contact(name:name, mobile:mobile).validate() == valid
		where:
			name    | mobile | valid
			null    | null   | false
			null    | '123'  | false
			''      | '123'  | true
			'a'     | null   | true
			'a'     | '123'  | true
			'a'*256 | '123'  | false
	}
	
	@Unroll
	def "test notes constraints"() {
		expect:
			new Contact(notes:notes, name:"Tim").validate() == valid
		where:
			notes      | valid
			null       | true
			''         | true
			'a' * 1024 | true
			'a' * 1025 | false
	}
	
	@Unroll
	def "test email address constraints"() {
		expect:
			new Contact(email:email, name:'alice', mobile:'123').validate() == valid
		where:
			email              | valid
			null               | true
			'yaya'             | false
			'yaya@'            | false
			'yaya@example.com' | true
	}

	def "blank names are allowed so long as there is a number"() {
		when:
			def noNameContact = new Contact(name:'', mobile:'9876543')
			def namedContact = new Contact(name:'a')
			def noInfoContact = new Contact(name:'', mobile:'')
		then:
			noNameContact.validate()
			namedContact.validate()
			!noInfoContact.validate()
	}

	def "duplicate names are allowed"(){
		when:
			def Contact contact1 = new Contact(name:'John')
			def Contact contact2 = new Contact(name:'John')
		then:
			contact1.save()
			contact2.save()
	}

	def 'contact may have a custom field'() {
		when:
			Contact c = new Contact(name: 'Eve')
			c.addToCustomFields(new CustomField())
		then:
			c.validate()
	}

	def 'contact may have multiple custom fields'() {
		when:
			Contact c = new Contact(name: 'Eve')
			c.addToCustomFields(new CustomField())
			c.addToCustomFields(new CustomField())
		then:
			c.validate()
	}

  	def "should return the count as zero is there is no address present for a given contact"() {
		when:
			def inboundMessagesCount = new Contact(name:"Person without an address").inboundMessagesCount
			def outboundMessagesCount = new Contact(name:"Person without an address").outboundMessagesCount
		then:
			inboundMessagesCount == 0
			outboundMessagesCount == 0
	}
}

