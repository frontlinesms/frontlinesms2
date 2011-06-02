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
}
