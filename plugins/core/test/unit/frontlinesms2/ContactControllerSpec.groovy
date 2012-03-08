package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ContactControllerSpec extends ControllerSpec {
	
	def "should render model containing shared and unshared groups"() {
		setup:
			def alice = new Contact(name: "Alice", primaryMobile: "12345")
			def bob = new Contact(name: "Bob", primaryMobile: "54321")
			mockDomain(Contact, [alice, bob])
			def group1 = new Group(name: "group1")
			def group2 = new Group(name: "group2")
			mockDomain(Group, [group1, group2])
			def groupCriteria = [list: {Closure c-> [group1]}]
			registerMetaClass(Group)
			Group.metaClass.static.createCriteria = {groupCriteria}
			mockDomain GroupMembership, [new GroupMembership(group: Group.findByName('group1'), contact: alice),
					new GroupMembership(group: Group.findByName('group1'), contact: bob) ,
					new GroupMembership(group: Group.findByName('group2'), contact: bob)]
			mockParams.checkedContactList = "$alice.id, $bob.id"
		when:
			controller.multipleContactGroupList()
			def model = controller.modelAndView.model
		then:
			model.sharedGroupInstanceList == [Group.findByName('group1')]
			model.nonSharedGroupInstanceList == [Group.findByName('group2')]
	}
}

