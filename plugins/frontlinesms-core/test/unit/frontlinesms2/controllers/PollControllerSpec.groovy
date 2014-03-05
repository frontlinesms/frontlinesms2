package frontlinesms2.controllers

import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*
import grails.buildtestdata.mixin.Build

@TestFor(PollController)
@Mock([Contact, TextMessage, Group, GroupMembership, Poll, SmartGroup, PollResponse])
@Build(TextMessage)
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

	def "can unarchive a poll"() {
		given:
			Activity.metaClass.static.get = { Serializable s -> Poll.get(s) }
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

