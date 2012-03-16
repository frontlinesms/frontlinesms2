package frontlinesms2

class PollSpec extends grails.plugin.spock.UnitSpec {
	/** some responses that should pass validation */
	def OK_RESPONSES = [new PollResponse(value: "one"), new PollResponse(value: "two")]
	
	def setup() {
		registerMetaClass Poll
		Poll.metaClass.static.withCriteria = { null } // this allows validation of 'title' field to pass
		Poll.metaClass.static.findByTitleIlike = { null }
	}
	
	def 'Poll must have at least two responses'() {
		given:
			mockDomain(Poll)
		when:
			def p = new Poll(name:'test poll')
		then:
			!p.validate()
		when:
			p.addToResponses(new PollResponse(value:'one'))
		then:
			!p.validate()
		when:
			p.addToResponses(new PollResponse(value:'two'))
		then:
			p.validate()
		when:
			p.addToResponses(new PollResponse(value:'three'))
		then:
			p.validate()
	}

	def "poll auto-reply cannot be blank"() {
		setup:
			mockDomain(Poll)
		when:
			def poll = new Poll(title:"title", autoReplyText:" ", responses:OK_RESPONSES)
		then:
			!poll.validate()
	}
	
	def "poll with auto-reply can be edited"() {
		setup:
			mockDomain(Poll)
		when:
			def poll = Poll.createPoll([name:"title", autoReplyText:"thanks for participaping", choiceA:"one", choiceB:"two"])
		then:
			poll.save()
		when:
			poll = Poll.editPoll(poll.id, [autoReplyText:"thanks for participating"])
		then:
			poll
		when:
			poll = Poll.editPoll(poll.id, [autoReplyText:null])
		then:
			poll
		when:
			poll = Poll.editPoll(poll.id, [autoReplyText:""])
		then:
			poll
	}
	
	def "can edit poll keyword if no other poll with that keyword exists"() {
		given:
			mockDomain(Poll)
			Poll poll1 = Poll.createPoll([name:"poll1", autoReplyText:"thanks for participaping", choiceA:"one", choiceB:"two", keyword: new Keyword(value:"test")])
			def returnedPoll
		when:
			def poll2 = Poll.createPoll([name:"poll2",choiceA:"one", choiceB:"two", keyword: new Keyword(value:"testing")])
		then:
			poll2
		when:
			returnedPoll = Poll.editPoll(poll2.id, [keyword:"test"])
		then:
			returnedPoll.hasErrors()
		when:
			returnedPoll = Poll.editPoll(poll2.id, [keyword:"TEST"])
		then:
			returnedPoll.hasErrors()
		when:
			returnedPoll = Poll.editPoll(poll2.id, [keyword:"TesT"])
		then:
			returnedPoll.hasErrors()
		when:
			returnedPoll = Poll.editPoll(poll2.id, [keyword:"anotherkey"])
		then:
			!returnedPoll.hasErrors()
	}
}
