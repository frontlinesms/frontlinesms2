package frontlinesms2

import spock.lang.*
import grails.test.mixin.*
import grails.buildtestdata.mixin.Build

@TestFor(PollController)
@Mock([Contact, Fmessage, Group, GroupMembership, Poll, SmartGroup, PollResponse])
@Build(Fmessage)
class PollControllerSpec extends Specification {
	def setup() {
		Group.metaClass.getMembers = {
			GroupMembership.findAllByGroup(delegate)*.contact.unique().sort { it.name }
		}
		// Not sure why this is necessary with Test Mixins, but it seems to be:
		PollResponse.metaClass.addToMessages = { m ->
			if(delegate.messages) delegate.messages << m
			else delegate.messages = [m]
			return delegate
		}
		PollResponse.metaClass.removeFromMessages = { m ->
			if(delegate.messages) delegate.messages -= m
			return delegate
		}
	}

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
			model.groupList["group-$group1.id"] == [name:"group1", addresses:["12345", "54321"]]
			model.groupList["group-$group2.id"] == [name:"group2", addresses:["54321"]]
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

	def "save action should persist outgoing message when required"() {
		given:
			MessageSendJob.metaClass.static.defer = { Fmessage message -> }
			MessageSendService sendService = Mock()
			sendService.createOutgoingMessage(_) >> { Map params -> Fmessage.buildWithoutSave() }
			controller.messageSendService = sendService
			params.name = "Test"
			params.question = "Are we having fun?"
			params.pollType = "yesNo"
			params.messageText = "thanks for participating in this poll"
			params.addresses = "07257723729"
		when:
			controller.save()
		then:
			Fmessage.count() == 1
	}
	
}
