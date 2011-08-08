package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class PollControllerSpec extends ControllerSpec {

	def "default action is CREATE"() {
		when:
			controller.index()
		then:
			controller.redirectArgs.controller == 'poll' || !controller.redirectArgs.controller
			controller.redirectArgs.action == 'create'
	}

	def "test create"() {
		setup:
			def alice = new Contact(name: "Alice", primaryMobile: "12345")
			def bob = new Contact(name: "Bob", primaryMobile: "54321")
			mockDomain(Contact, [alice, bob])
			mockDomain(Group, [new Group(name: "group1", members: [alice, bob]), new Group(name: "group2",  members: [bob])])
		when:
			def resultMap = controller.create()
		then:
			resultMap['contactList']*.name == ["Alice", "Bob"]
			resultMap['groupList']["group1"].containsAll(["12345", "54321"])
			resultMap['groupList']["group2"].containsAll(["54321"])


	}
}

