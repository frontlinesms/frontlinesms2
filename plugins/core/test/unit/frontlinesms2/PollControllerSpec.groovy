package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class PollControllerSpec extends ControllerSpec {

	def "create action should provide groups and contacts for recipients list"() {
		setup:
			def alice = new Contact(name: "Alice", mobile: "12345")
			def bob = new Contact(name: "Bob", mobile: "54321")
			mockDomain(Contact, [alice, bob])
			mockDomain(Group, [new Group(name: "group1"), new Group(name: "group2")])
			mockDomain SmartGroup, []
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
	
	def "can unarchive a poll"() {
		given:
			registerMetaClass PollController
			registerMetaClass Fmessage
			mockDomain(Poll)
			mockDomain(Fmessage)
			Fmessage.metaClass.static.owned = { Poll p, Boolean b, Boolean c ->
				Fmessage
			}
			PollController.metaClass.withActivity = { Closure c -> c.call(Poll.get(mockParams.id)) }
			def poll = new Poll(name:'thingy', archived:true)
			poll.editResponses(choiceA:'One', choiceB:'Other')
			poll.save()
			assert poll.archived
		when:
			mockParams.id = poll.id
			controller.unarchive()
		then:
			!poll.archived
			controller.redirectArgs == [controller:'archive', action:'activityList']
	}
}

