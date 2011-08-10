package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class PollControllerSpec extends ControllerSpec {

//	def "default action is CREATE"() {
//		when:
//			controller.index()
//		then:
//			controller.redirectArgs.controller == 'poll' || !controller.redirectArgs.controller
//			controller.redirectArgs.action == 'create'
//	}
	
	def "should list all the polls"() {
		mockDomain(Poll, [new Poll(archived: true), new Poll(archived: false), new Poll(archived : true)])
		controller.params.archived = true
		when:
			def results = controller.index()
		then:
			results['polls'].every {it.archived}
			results['actionLayout'] == 'archive'
			results['messageSection'] == 'poll'
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

	def "should archive a poll"() {
		setup:
			mockDomain(Poll, [new Poll(id: 2L, archived: false)])
			controller.params.id = 2L
		when:
			controller.archive()
		then:
			Poll.get(2).archived
			redirectArgs.controller == "message"
			redirectArgs.action == "inbox"
			controller.flash.message
	}
}

