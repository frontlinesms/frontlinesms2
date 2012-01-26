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
			controller.params.autoReplyText = "automatic reply text"
		when:
			controller.save()
			def poll = Poll.findByName("poll")
		then:
			poll
			poll.autoReplyText == "automatic reply text"
			(poll.responses*.value).containsAll(['yes', 'no', 'maybe'])
	}

	def "saving new poll with keyword enabled should save the keyword"() {
		given:
			controller.params.name = 'test-poll-1'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoReplyText = "automatic reply text"
			controller.params.enableKeyword = true
			controller.params.keyword = "hello"
		when:
			controller.save()
		then:
			Poll.findByName("test-poll-1")?.keyword == 'HELLO'
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
			def poll = Poll.createPoll(name: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris').save(failOnError:true, flush:true)
		when:
			assert Poll.findAllByArchived(false) == [poll]
			poll.archived = true;
		then:
			Poll.findAllByArchived(true) == [poll]
			Poll.findAllByArchived(false) == []
	}

	def  "should update a given poll object"() {
		setup:
			Poll.createPoll(name: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
			def poll = Poll.findByName("Who is badder?")
			controller.params.id = poll.id
			controller.params.name = "renamed poll name"
		when:
			controller.update()
			def updatedPoll = Poll.get(poll.id)
		then:
			controller.response.redirectedUrl == "/message/activity/${poll.id}"
			updatedPoll.name == "renamed poll name"
			updatedPoll.question == "question"
			updatedPoll.autoReplyText == "Thanks"
	}
	
	def "can delete a poll to send it to the trash"() {
		setup:
			def poll = Poll.createPoll(name: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
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
			def poll = Poll.createPoll(name: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
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
			model.groupList == [ "English numbers": [] ]
				
	}
}
