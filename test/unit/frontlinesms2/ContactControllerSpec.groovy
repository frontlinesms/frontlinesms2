package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ContactControllerSpec extends ControllerSpec {
	def "adding and removing a contact from the same group triggers error"() {
		given:
			def c = new Contact(name:'Ada')
			mockDomain(Contact, [c])
			mockDomain(Group)
			mockDomain(GroupMembership)
			mockParams.contactId = c.id
			mockParams.groupsToAdd = ",2,"
			mockParams.groupsToRemove = ",2,"
		when:
			controller.update()
		then:
			controller.modelAndView.model.contactInstance.errors
	}

	def "All contacts list appears in alphabetical order"() {
		setup:
			def contact1 = new Contact(name:'Bob')
			def contact2 = new Contact(name:'Alice')
			def contact3 = new Contact(name:'Charlie')
			mockDomain(Contact, [contact1, contact2, contact3])
			mockDomain(Group)
			mockDomain(GroupMembership)
			mockParams.contactId = contact1.id
			assert controller.show() != null
		when:
			def model = controller.show()
		then:
			model.contactInstanceList == [contact2, contact1, contact3]
			model.contactInstanceList != [contact1, contact2, contact3]
	}
}

