package frontlinesms2


class PollControllerIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def setup() {
		controller = new PollController()
	}

	def "can save new poll"() {
		setup:
			controller.params.title = "poll"
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoReplyText = "automatic reply text"
		when:
			controller.save()
			def poll = Poll.findByTitle("poll")
		then:
			poll
			poll.autoReplyText == "automatic reply text"
			(poll.responses*.value).containsAll(['yes', 'no', 'maybe'])
	}

	def "saving new poll with keyword enabled should save the keyword"() {
		given:
			controller.params.title = 'test-poll-1'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoReplyText = "automatic reply text"
			controller.params.enableKeyword = true
			controller.params.keyword = "hello"
		when:
			controller.save()
		then:
			Poll.findByTitle("test-poll-1")?.keyword == 'HELLO'
	}

	def "saving new poll with keyword disabled does should not save the keyword"() {
		given:
			controller.params.title = 'test-poll-2'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoReplyText = "automatic reply text"
			controller.params.enableKeyword = false
			controller.params.keyword = "goodbye"
		when:
			controller.save()
			def p = Poll.findByTitle("test-poll-2")
			println "From the database, p.keyword is $p.keyword"
		then:
			p
			!p.keyword
	}
	
	def "can archive a poll"() {
		setup:
			def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris').save(failOnError:true, flush:true)
		when:
			assert Poll.getNonArchivedPolls() == [poll]
			poll.archived = true;
		then:
			Poll.getArchivedPolls() == [poll]
			Poll.getNonArchivedPolls() == []
	}

	def  "should update a given poll object"() {
		setup:
			Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
			def poll = Poll.findByTitle("Who is badder?")
			controller.params.id = poll.id
			controller.params.title = "renamed poll name"
		when:
			controller.update()
			def updatedPoll = Poll.get(poll.id)
		then:
			controller.response.redirectedUrl == "/message/poll/${poll.id}"
			updatedPoll.title == "renamed poll name"
			updatedPoll.question == "question"
			updatedPoll.autoReplyText == "Thanks"
	}
}
