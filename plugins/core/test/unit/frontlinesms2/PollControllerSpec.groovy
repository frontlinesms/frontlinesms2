package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(PollController)
@Mock([Contact, Fmessage, Group, GroupMembership, Poll, SmartGroup])
class PollControllerSpec extends Specification {
	def "create action should provide groups and contacts for recipients list"() {
		setup:
			def alice = new Contact(name: "Alice", mobile: "12345")
			def bob = new Contact(name: "Bob", mobile: "54321")
			[alice, bob]*.save()
			def group1 = new Group(name:'group1').save()
			def group2 = new Group(name:'group2').save()
			[new GroupMembership(group:group1, contact:alice),
				new GroupMembership(group:group1, contact:bob),
				new GroupMembership(group:group2, contact:bob)]*.save()
		when:
                        def model = controller.create()
                then:
                        model.contactList*.name == ["Alice", "Bob"]
                        model.groupList.group1.containsAll(["12345", "54321"])
                        model.groupList.group2.containsAll(["54321"])
	}
	
	def "can unarchive a poll"() {
		given:
			PollController.metaClass.withActivity = { Closure c -> c.call(Poll.get(params.id)) }
			def poll = new Poll(name:'thingy', archived:true)
			poll.editResponses(choiceA:'One', choiceB:'Other')
			poll.save()
			assert poll.archived
		when:
			params.id = poll.id
			controller.unarchive()
		then:
			!poll.archived
			controller.response.redirectUrl == '/archive/activityList'
	}
}

