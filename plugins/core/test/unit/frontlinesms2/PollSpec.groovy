package frontlinesms2

class PollSpec extends grails.plugin.spock.UnitSpec {
	/** some responses that should pass validation */
	def OK_RESPONSES = [new PollResponse(value: "one"), new PollResponse(value: "two")]
	
	def setup() {
		registerMetaClass Poll
		Poll.metaClass.static.withCriteria = { null } // this allows validation of 'title' field to pass
		Poll.metaClass.static.findByTitleIlike = { null }
	}
	
	def 'Poll must have at least three responses'() {
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
			!p.validate()
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
}
