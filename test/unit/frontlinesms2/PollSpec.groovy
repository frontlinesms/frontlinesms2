package frontlinesms2

class PollSpec extends grails.plugin.spock.UnitSpec {
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
			def poll = new Poll(title: "title", autoReplyText: " ", responses : [new PollResponse(value: "one"), new PollResponse(value: "two")])
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
}
