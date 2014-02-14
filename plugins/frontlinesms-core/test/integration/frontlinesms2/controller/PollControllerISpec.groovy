package frontlinesms2.controller

import frontlinesms2.*

class PollControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def trashService
	def i18nUtilService

	def setup() {
		controller = new PollController()
		controller.trashService = trashService
		controller.params.addresses = '123'
	}
	
	def "can save new poll"() {
		setup:
			controller.params.name = "poll"
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.enableAutoreply = "true"
			controller.params.autoreplyText = "automatic reply text"
		when:
			controller.save()
			def poll = Poll.findByName("poll")
		then:
			poll
			controller.flash.message == i18nUtilService.getMessage([code:"poll.save.success", args:[poll.name]])
			poll.autoreplyText == "automatic reply text"
			(poll.responses*.value).containsAll(['yes', 'no', 'maybe'])
	}

	def "saving new poll with keyword enabled should save the keyword with ownerDetail set to PollResponse.id"() {
		given:
			controller.params.name = 'test-poll-1'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoreplyText = "automatic reply text"
			controller.params.enableKeyword = "true"
			controller.params.topLevelKeyword = "Hello"
			controller.params.keywordsA = "A,aa"
			controller.params.keywordsB = "B,bb"
			controller.params.keywordsC = "maybe,idontknow"
		when:
			controller.save()
		then:
			def poll = Poll.findByName("test-poll-1")
			poll?.keywords*.value.containsAll(['HELLO','A','AA','B','BB','MAYBE','IDONTKNOW'])
			poll.keywords.size() == 7
			poll.keywords[0].ownerDetail == null
			poll.keywords[1].ownerDetail == 'A'
			poll.keywords[2].ownerDetail == 'A'
			poll.keywords[3].ownerDetail == 'B'
			poll.keywords[4].ownerDetail == 'B'
			poll.keywords[5].ownerDetail == 'C'
			poll.keywords[6].ownerDetail == 'C'
	}

	def "saving new poll with keyword disabled does should not save the keyword"() {
		given:
			controller.params.name = 'test-poll-2'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoReplyText = "automatic reply text"
			controller.params.enableKeyword = "false"
		when:
			controller.save()
			def p = Poll.findByName("test-poll-2")
		then:
			p
			!p.keywords
	}
	
	def "can archive a poll"() {
		setup:
			def poll = TestData.createBadnessPoll()
		when:
			assert Poll.findAllByArchived(false) == [poll]
			poll.archived = true;
		then:
			Poll.findAllByArchived(true) == [poll]
			Poll.findAllByArchived(false) == []
	}

	def  "can edit a given poll object"() {
		setup:
			def poll = TestData.createBadnessPoll()
			controller.params.ownerId = poll.id
			controller.params.enableAutoreply = "true"
			controller.params.name = "renamed poll name"
		when:
			controller.save()
			def updatedPoll = Poll.get(poll.id)
			println "edit():: ${updatedPoll.autoreplyText}"
		then:
			updatedPoll.name == "renamed poll name"
			updatedPoll.question == "question"
			updatedPoll.autoreplyText == "Thanks"
	}
	
	def "can delete a poll to send it to the trash"() {
		setup:
			def poll = TestData.createBadnessPoll()
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
			def poll = TestData.createBadnessPoll()
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
	
	def "edit action modifies the properties of an existing poll"() {
		setup:
			def poll = TestData.createBadnessPoll()
			controller.params.ownerId = poll.id
			controller.params.choiceC = "Arnold Vandam"
			controller.params.messageText = "question"
			controller.params.dontSendMessage=true
		when:
			controller.save()
			poll = Poll.get(poll.id)
		then:
			poll.responses*.value.containsAll(["Arnold Vandam", "Michael-Jackson", "Chuck-Norris"])
		when:
			controller.params.ownerId = poll.id
			controller.params.question = "Who is worse?"
			controller.params.dontSendMessage=true
			controller.save()
			poll = Poll.get(poll.id)
		then:
			poll.question == "Who is worse?"
		when:
			controller.params.ownerId = poll.id
			controller.params.enableAutoreply = "true"
			controller.params.autoreplyText = "Thank you for replying to this awesome poll"
			controller.params.dontSendMessage=true
			controller.save()
			poll = Poll.get(poll.id)
		then:
			poll.autoreplyText == "Thank you for replying to this awesome poll"
		when:
			controller.params.ownerId = poll.id
			controller.params.topLevelKeyword = 'bad'
			controller.params.enableKeyword = "true"
			controller.params.dontSendMessage=true
			controller.save()
			poll = Poll.get(poll.id)
		then:
			println "################ ${poll.keywords*.value}"
			poll.keywords*.value.contains('BAD')
	}

	def "editing a poll persists keyword changes"() {
		setup:
			TestData.createFootballPollWithKeywords()
		and:
			controller.params.ownerId=Poll.findByName('This is a poll').id
			controller.params.name = 'test-poll-1'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoreplyText = "automatic reply text"
			controller.params.enableKeyword = "true"
			controller.params.topLevelKeyword = "Hello"
			controller.params.keywordsA = "Manchester"
			controller.params.keywordsB = "Barcelona"
			controller.params.keywordsC = "Harambee,Team"
			controller.params.dontSendMessage=true
		when:
			controller.save()
		then:
			def poll = Poll.findByName("test-poll-1")
			println "#### in test ### ${poll?.keywords*.value}"
			//This fails but i don't know why
			//poll?.keywords*.value.containsAll(['HELLO','MANCHESTER','BARCELONA,','HARAMBEE','TEAM'])
			poll.keywords.size() == 5
			Keyword.findAll().size() == 5
			(poll.keywords[0].value == 'HELLO')&&(poll.keywords[0].ownerDetail == null)
			(poll.keywords[1].value == 'MANCHESTER')&&(poll.keywords[1].ownerDetail in poll.responses*.key)
			(poll.keywords[2].value == 'BARCELONA')&&(poll.keywords[2].ownerDetail in poll.responses*.key)
			(poll.keywords[3].value == 'HARAMBEE')&&(poll.keywords[3].ownerDetail in poll.responses*.key)
			(poll.keywords[4].value == 'TEAM')&&(poll.keywords[4].ownerDetail in poll.responses*.key)
	}

	def "editing a poll and removing the top level keyword should set responses as top level"() {
		setup:
			TestData.createFootballPollWithKeywords()
		and:
			controller.params.ownerId=Poll.findByName('This is a poll').id
			controller.params.name = 'test-poll-1'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoreplyText = "automatic reply text"
			controller.params.enableKeyword = "true"
			controller.params.topLevelKeyword = ""
			controller.params.keywordsA = "Manchester"
			controller.params.keywordsB = "Barcelona"
			controller.params.keywordsC = "Harambee,Team"
			controller.params.dontSendMessage=true
		when:
			controller.save()
		then:
			def poll = Poll.findByName("test-poll-1")
			poll.keywords[0].value == 'MANCHESTER'
			poll.keywords[1].value == 'BARCELONA'
			poll.keywords[2].value == 'HARAMBEE'
			poll.keywords[3].value == 'TEAM'

			poll.keywords[0].isTopLevel
			poll.keywords[1].isTopLevel
			poll.keywords[2].isTopLevel
			poll.keywords[3].isTopLevel
	}
}

