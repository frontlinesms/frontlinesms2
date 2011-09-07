package frontlinesms2

class PollSpec extends grails.plugin.spock.UnitSpec {
	/** some responses that should pass validation */
	def OK_RESPONSES = [new PollResponse(value: "one"), new PollResponse(value: "two")]
	
	def setup() {
		registerMetaClass Poll
		Poll.metaClass.static.withCriteria = { null } // this allows validation of 'title' field to pass
	}
	
	def 'Poll must have at least two responses'() {
		given:
			mockDomain(Poll)
		when:
			def p = new Poll(title:'test poll')
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

	def "should fetch all non archived polls"() {
		mockDomain(Poll, [new Poll(archived: true), new Poll(archived: false), new Poll(archived: false)])
		when:
			def results = Poll.getNonArchivedPolls()
		then:
			results.every {!it.archived}
	}
	
	def "Poll keyword may not contain whitespace"() {
		given:
			mockForConstraintsTests(Poll)
		when:
			def p = new Poll(keyword:'with space')
		then:
			!p.validate()
	}
	
	def "Poll keyword should be unique, ignoring case, among unarchived polls"() {
		given:
			mockForConstraintsTests(Poll, [new Poll(keyword:'something')])
		when:
			Poll p = new Poll(keyword:'someTHING', title:'test', responses:OK_RESPONSES)
		then:
			!p.validate()
	}
	
	def "Poll keyword should not be unique between archived polls"() {
		given:
			mockDomain(Poll)
			Poll p1 = new Poll(keyword:'something', archived:true).save(failOnError:true)
		when:
			Poll p2 = new Poll(keyword:'someTHING', archived:true)
		then:
			p2.validate()
	}
	
	def "Poll keyword in unarchived poll may be the same as that in an archived poll"() {
		given:
			mockDomain(Poll)
			Poll p1 = new Poll(keyword:'something', archived:true).save(failOnError:true)
		when:
			Poll p2 = new Poll(keyword:'someTHING', archived:false)
		then:
			p2.validate()
	}
}
