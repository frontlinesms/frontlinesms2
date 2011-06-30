package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ContactControllerSpec extends ControllerSpec {
	def "All contacts list appears in alphabetical order"() {
		setup:
			def contact1 = new Contact(name:'Bob')
			def contact2 = new Contact(name:'Alice')
			def contact3 = new Contact(name:'Charlie')
			mockDomain(Contact, [contact1, contact2, contact3])
			mockDomain(Group)
			mockDomain(CustomField)
			registerMetaClass(CustomField)
			CustomField.metaClass.'static'.getAllUniquelyNamed = {-> new CustomField()}
			mockParams.contactId = contact1.id
		when:
			def result = controller.show()
		then:
			result
			result.contactInstanceList == [contact2, contact1, contact3]
			result.contactInstanceList != [contact1, contact2, contact3]
	}
}

