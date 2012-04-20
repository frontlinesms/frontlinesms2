package frontlinesms2.controller

import frontlinesms2.*

class PollControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def setup() {
		controller = new PollController()
	}

	def "can save new poll"() {
		setup:
			controller.params.name = "poll"
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoreplyText = "automatic reply text"
		when:
			controller.save()
			def poll = Poll.findByName("poll")
		then:
			poll
			poll.autoreplyText == "automatic reply text"
			(poll.responses*.value).containsAll(['yes', 'no', 'maybe'])
	}

	def "saving new poll with keyword enabled should save the keyword"() {
		given:
			controller.params.name = 'test-poll-1'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoreplyText = "automatic reply text"
			controller.params.enableKeyword = true
			controller.params.keyword = "HELLO"
		when:
			controller.save()
		then:
			Poll.findByName("test-poll-1")?.keyword.value == 'HELLO'
	}

	def "saving new poll with keyword disabled does should not save the keyword"() {
		given:
			controller.params.name = 'test-poll-2'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoReplyText = "automatic reply text"
			controller.params.enableKeyword = false
			controller.params.keyword = "goodbye"
		when:
			controller.save()
			def p = Poll.findByName("test-poll-2")
		then:
			p
			!p.keyword
	}
	
	def "can archive a poll"() {
		setup:
			def poll = new Poll(name: 'Who is badder?', question: "question", autoReplyText: "Thanks")
			poll.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll.addToResponses(PollResponse.createUnknown())
			poll.save(failOnError:true, flush:true)
		when:
			assert Poll.findAllByArchived(false) == [poll]
			poll.archived = true;
		then:
			Poll.findAllByArchived(true) == [poll]
			Poll.findAllByArchived(false) == []
	}

	def  "can edit a given poll object"() {
		setup:
			def poll = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
			poll.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll.addToResponses(PollResponse.createUnknown())
			poll.save(failOnError:true, flush:true)
			controller.params.ownerId = poll.id
			controller.params.name = "renamed poll name"
		when:
			controller.save()
			def updatedPoll = Poll.get(poll.id)
		then:
			updatedPoll.name == "renamed poll name"
			updatedPoll.question == "question"
			updatedPoll.autoreplyText == "Thanks"
	}
	
	def "can delete a poll to send it to the trash"() {
		setup:
			def poll = new Poll(name: 'Who is badder?', question: "question", autoReplyText: "Thanks")
			poll.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll.addToResponses(PollResponse.createUnknown())
			poll.save(failOnError:true, flush:true)
		when:
			assert Poll.findAllByDeleted(false) == [poll]
			controller.params.id  = poll.id
			controller.delete()
		then:
			Poll.findAllByDeleted(true) == [poll]
			Poll.findAllByDeleted(false) == []
	}
	
	def "can restore a poll to move out of the trash"() {
		setup:
			def poll = new Poll(name: 'Who is badder?', question: "question", autoReplyText: "Thanks")
			poll.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll.addToResponses(PollResponse.createUnknown())
			poll.deleted = true
			poll.save(failOnError:true, flush:true)
		when:
			assert Poll.findAllByDeleted(true) == [poll]
			controller.params.id  = poll.id
			controller.restore()
		then:
			Poll.findAllByDeleted(false) == [poll]
			Poll.findAllByDeleted(true) == []
	}
	
	def "list of smart groups should be included in the group list"() {
		given:
			def s = new SmartGroup(name:'English numbers', mobile:'+44').save(flush:true)
		when:
			def model = controller.create()
		then:
			model.groupList["smartgroup-$s.id"] == []
				
	}
	
	def "edit action modifies the properties of an existing poll"() {
		setup:
			def poll = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
			poll.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll.addToResponses(PollResponse.createUnknown())
			poll.save(failOnError:true, flush:true)
			controller.params.ownerId = poll.id
			controller.params.choiceC = "Arnold Vandam"
		when:
			controller.save()
			poll = Poll.get(poll.id)
		then:
			poll.responses*.value.containsAll(["Arnold Vandam", "Michael-Jackson", "Chuck-Norris"])
		when:
			controller.params.ownerId = poll.id
			controller.params.question = "Who is worse?"
			controller.save()
			poll = Poll.get(poll.id)
		then:
			poll.question == "Who is worse?"
		when:
			controller.params.ownerId = poll.id
			controller.params.autoreplyText = "Thank you for replying to this awesome poll"
			controller.save()
			poll = Poll.get(poll.id)
		then:
			poll.autoreplyText == "Thank you for replying to this awesome poll"
		when:
			controller.params.ownerId = poll.id
			controller.params.keyword = 'bad'
			controller.params.enableKeyword = true
			controller.save()
			poll = Poll.get(poll.id)
		then:
			poll.keyword.value == 'bad'
	}
	
}
