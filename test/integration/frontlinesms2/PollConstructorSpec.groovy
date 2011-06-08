package frontlinesms2

class PollConstructorSpec extends grails.plugin.spock.IntegrationSpec {
	def "creating a new poll also creates a poll response with value 'Unknown'"() {
		when:
			def responseList = [new PollResponse(value:'one'), new PollResponse(value:'other')]
			def p = Poll.createPoll('This is a poll', responseList)
		then:
			p.responses.size() == 3

	}
}