package frontlinesms2

class PollConstructorSpec extends grails.plugin.spock.IntegrationSpec {
	def "creating a new poll also creates a poll response with value UNKNOWN"() {
		when:
			def responseList = [new PollResponse(value:'One'), new PollResponse(value:'Other')]
			def p = Poll.createPoll('This is a poll', responseList)
		then:
			p.responses.size() == 3
			p.responses*.value.sort() == ['One', 'Other', 'Unknown']
	}
}
