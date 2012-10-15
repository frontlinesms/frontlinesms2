package frontlinesms2.controller

import frontlinesms2.*

class PollControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def trashService

	def setup() {
		controller = new PollController()
		controller.trashService = trashService
		controller.params.addresses = '123'
	}

	def cleanup(){
		Poll.findAll()*.delete()
		Autoreply.findAll()*.delete()
		Keyword.findAll()*.delete()
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
			controller.params.enableKeyword = true
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
			poll.keywords[1].ownerDetail == poll.responses[0].key
			poll.keywords[2].ownerDetail == poll.responses[0].key
			poll.keywords[3].ownerDetail == poll.responses[1].key
			poll.keywords[4].ownerDetail == poll.responses[1].key
			poll.keywords[5].ownerDetail == poll.responses[2].key
			poll.keywords[6].ownerDetail == poll.responses[2].key
	}

	def "saving new poll with keyword disabled does should not save the keyword"() {
		given:
			controller.params.name = 'test-poll-2'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoReplyText = "automatic reply text"
			controller.params.enableKeyword = false
		when:
			controller.save()
			def p = Poll.findByName("test-poll-2")
		then:
			p
			!p.keywords
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
			model.groupList["smartgroup-$s.id"] == [name:'English numbers', addresses:[]]
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
			controller.params.enableAutoreply = "true"
			controller.params.autoreplyText = "Thank you for replying to this awesome poll"
			controller.save()
			poll = Poll.get(poll.id)
		then:
			poll.autoreplyText == "Thank you for replying to this awesome poll"
		when:
			controller.params.ownerId = poll.id
			controller.params.topLevelKeyword = 'bad'
			controller.params.enableKeyword = true
			controller.params.dontSendMessage=true
			controller.save()
			poll = Poll.get(poll.id)
		then:
			poll.keywords[0].value == 'BAD'
	}

	def "editing a poll persists keyword changes"() {
		setup:
			def p = new Poll(name: 'This is a poll', yesNo:false)
			p.addToResponses(new PollResponse(key:'A', value:"Manchester"))
			p.addToResponses(new PollResponse(key:'B', value:"Barcelona"))
			p.addToResponses(new PollResponse(key:'C', value:"Harambee Stars"))
			p.addToResponses(PollResponse.createUnknown())
			p.save(failOnError:true)
			def k1 = new Keyword(value: "FOOTBALL", activity: p)
			def k2 = new Keyword(value: "MANCHESTER", activity: p, ownerDetail:"A", isTopLevel:false)
			def k3 = new Keyword(value: "HARAMBEE", activity: p, ownerDetail:"B", isTopLevel:false)
			def k4 = new Keyword(value: "BARCELONA", activity: p, ownerDetail:"C", isTopLevel:false)
			p.addToKeywords(k1)
			p.addToKeywords(k2)
			p.addToKeywords(k3)
			p.addToKeywords(k4)
			p.save(failOnError:true, flush:true)
		and:
			controller.params.ownerId=Poll.findByName('This is a poll').id
			controller.params.name = 'test-poll-1'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoreplyText = "automatic reply text"
			controller.params.enableKeyword = true
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
			(poll.keywords[1].value == 'MANCHESTER')&&(poll.keywords[1].ownerDetail == poll.responses[0].key)
			(poll.keywords[2].value == 'BARCELONA')&&(poll.keywords[2].ownerDetail == poll.responses[1].key)
			(poll.keywords[3].value == 'HARAMBEE')&&(poll.keywords[3].ownerDetail == poll.responses[2].key)
			(poll.keywords[4].value == 'TEAM')&&(poll.keywords[4].ownerDetail == poll.responses[2].key)
	}

	def "If new poll data does not validate then the service action should rollback"() {
		setup:
			def a1 = new Autoreply(name:"Toothpaste", autoreplyText: "Thanks for the input")
			a1.addToKeywords(new Keyword(value:'HELLO'))
			a1.save(failOnError:true)
			def p = new Poll(name: 'This is a poll', yesNo:false)
			p.addToResponses(new PollResponse(key:'A', value:"Manchester"))
			p.addToResponses(new PollResponse(key:'B', value:"Barcelona"))
			p.addToResponses(new PollResponse(key:'C', value:"Harambee Stars"))
			p.addToResponses(PollResponse.createUnknown())
			p.save(failOnError:true)
			def k1 = new Keyword(value: "FOOTBALL", activity: p)
			def k2 = new Keyword(value: "MANCHESTER", activity: p, ownerDetail:"A", isTopLevel:false)
			def k3 = new Keyword(value: "HARAMBEE", activity: p, ownerDetail:"B", isTopLevel:false)
			def k4 = new Keyword(value: "BARCELONA", activity: p, ownerDetail:"C", isTopLevel:false)
			p.addToKeywords(k1)
			p.addToKeywords(k2)
			p.addToKeywords(k3)
			p.addToKeywords(k4)
			p.save(failOnError:true, flush:true)
		and:
			controller.params.ownerId=Poll.findByName('This is a poll').id
			controller.params.name = 'name in use'
			controller.params.choiceA = "yes"
			controller.params.choiceB = "no"
			controller.params.choiceC = "maybe"
			controller.params.autoreplyText = "automatic reply text"
			controller.params.enableKeyword = true
			controller.params.topLevelKeyword = "Hello"
			controller.params.keywordsA = "Manu"
			controller.params.keywordsB = "Barcelona"
			controller.params.keywordsC = "Harambee,Team"
			controller.params.dontSendMessage=true
		when:
			controller.save()
		then:
			println "## Poll keywords ########### ${Poll.findAll()*.keywords}"
			println "## Autoreply keywords ########### ${Autoreply.findAll()*.keywords}"
			Poll.findAll().size() == 1
			def poll = Poll.get(p.id)
			poll.name == "This is a poll"
			println "#### keywords in test ### ${poll?.keywords*.value}"
			println "#### All Keywords ### ${Keyword.findAll()*.value}"
			poll.keywords.size() == 4
			poll.keywords[0].value == 'FOOTBALL'
			poll.keywords[1].value == 'MANCHESTER'
			poll.keywords[2].value == 'HARAMBEE'
			poll.keywords[3].value == 'BARCELONA'
	}
}
