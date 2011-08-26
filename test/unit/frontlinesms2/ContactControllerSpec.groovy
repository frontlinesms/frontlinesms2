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
			mockDomain(GroupMembership)
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
	
	def "should render model containing shared and unshared groups"() {
		setup:
			def alice = new Contact(name: "Alice", primaryMobile: "12345")
			def bob = new Contact(name: "Bob", primaryMobile: "54321")
			mockDomain(Contact, [alice, bob])
			mockDomain(Group, [new Group(name: "group1"), new Group(name: "group2")])
			mockDomain GroupMembership, [new GroupMembership(group: Group.findByName('group1'), contact: alice),
				new GroupMembership(group: Group.findByName('group1'), contact: bob) ,
				new GroupMembership(group: Group.findByName('group2'), contact: bob)]
			mockParams.contactIds = "$alice.id, $bob.id"
		when:
			controller.multipleContactGroupList()
			def model = controller.modelAndView.model
		then:
			model.sharedGroupInstanceList == [Group.findByName('group1')]
			model.nonSharedGroupInstanceList == [Group.findByName('group2')]
	}
}

