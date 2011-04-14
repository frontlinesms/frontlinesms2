package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ContactSpec extends UnitSpec {
	def "contact may have a name"() {
		when:
			Contact c = new Contact()
			assert c.name == null
			c.name = 'Alice'
		then:
			c.name == 'Alice'
	}

	def "contact must have a name"() {
		when:
			def noNameContact = new Contact()
			def namedContact = new Contact(name:'Alice')
			mockForConstraintsTests(Contact, [noNameContact, namedContact])
		then:
			!noNameContact.validate()
			namedContact.validate()
	}
}

