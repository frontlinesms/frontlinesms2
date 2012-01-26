package frontlinesms2.domain

import frontlinesms2.*

class PollConstructorISpec extends grails.plugin.spock.IntegrationSpec {
	def "creating a new poll also creates a poll response with value UNKNOWN"() {
		when:
			def p = Poll.createPoll(name: 'This is a poll', choiceA:  'One', choiceB: 'Other', question:'A poll question')
		then:
			p.responses.size() == 3
			p.responses*.value.sort() == ['One', 'Other', 'Unknown']
			p.question == 'A poll question'
	}
}
