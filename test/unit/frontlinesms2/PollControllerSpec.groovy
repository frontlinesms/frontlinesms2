package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class PollControllerSpec extends ControllerSpec {

	def "should list all the polls"() {
		mockDomain(Poll, [new Poll(archived: true), new Poll(archived: false), new Poll(archived : true)])
		controller.params.viewingArchive = true
		when:
			def results = controller.index()
		then:
			results['polls'].every {it.archived}
			results['messageSection'] == 'poll'
	}

	def "test create"() {
		setup:
			def alice = new Contact(name: "Alice", primaryMobile: "12345")
			def bob = new Contact(name: "Bob", primaryMobile: "54321")
			mockDomain(Contact, [alice, bob])
			mockDomain(Group, [new Group(name: "group1"), new Group(name: "group2")])
			mockDomain GroupMembership, [new GroupMembership(group: Group.findByName('group1'), contact: alice),
				new GroupMembership(group: Group.findByName('group1'), contact: bob) ,
				new GroupMembership(group: Group.findByName('group2'), contact: bob)]
		when:
			def resultMap = controller.create()
		then:
			resultMap['contactList']*.name == ["Alice", "Bob"]
			resultMap['groupList']["group1"].containsAll(["12345", "54321"])
			resultMap['groupList']["group2"].containsAll(["54321"])
	}
}

