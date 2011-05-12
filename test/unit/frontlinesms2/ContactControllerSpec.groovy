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
			assert controller.list() != null
		when:
			def model = controller.list()
		then:
			model.contactInstanceList == [contact2, contact1, contact3]
			model.contactInstanceList != [contact1, contact2, contact3]
	}

//
//	def "Contact's group list appears in alphabetical order"() {
//		setup:
//			def contact = new Contact(name:'Alice')
//			def group1 = new Group(name:'Zebras')
//			def group2 = new Group(name:'Apes')
//			def group3 = new Group(name:'Mixed Breeds')
//			contact.addToGroups(group1)
//			contact.addToGroups(group2)
//			contact.addToGroups(group3)
//			mockDomain(Group, [group1, group2, group3])
//			mockDomain(Contact, [contact])
//		when:
//			def model = controller.show.${contact.id}()
//		then:
//			model.contactGroupInstanceList == [group2, group3, group1]
//			model.contactGroupInstanceList != [group1, group2, group3]
//	}
//
//	def "Contact's non-group dropdown  appear in alphabetical order"() {
//		setup:
//			def contact = new Contact(name:'Alice')
//			def group1 = new Group(name:'Zebras')
//			def group2 = new Group(name:'Apes')
//			def group3 = new Group(name:'Mixed Breeds')
//			def group4 = new Group(name:'Lions')
//			contact.addToGroups(group4)
//			mockDomain(Group, [group1, group2, group3, group4])
//			mockDomain(Contact, [contact])
//		when:
//			def model = controller.show.${contact.id}()
//		then:
//			model.nonContactGroupInstanceList == [group2, group3, group1]
//			model.nonContactGroupInstanceList != [group1, group2, group3]
//	}
}

