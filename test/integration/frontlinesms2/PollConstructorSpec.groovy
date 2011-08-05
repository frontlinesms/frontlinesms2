package frontlinesms2

class PollConstructorSpec extends grails.plugin.spock.IntegrationSpec {
	def "creating a new poll also creates a poll response with value UNKNOWN"() {
		when:
			def p = Poll.createPoll(title: 'This is a poll', choiceA:  'One', choiceB: 'Other', instruction:'Reply A, B to 12323')
		then:
			p.responses.size() == 3
			p.responses*.value.sort() == ['One', 'Other', 'Unknown']
			p.instruction == 'Reply A, B to 12323'
	}
}
