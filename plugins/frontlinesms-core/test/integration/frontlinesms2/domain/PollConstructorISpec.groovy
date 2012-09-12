package frontlinesms2.domain

import frontlinesms2.*

class PollConstructorISpec extends grails.plugin.spock.IntegrationSpec {
	def "creating a new poll also creates a poll response with value UNKNOWN"() {
		when:
			def p = new Poll(name: 'This is a poll',  question:'A poll question')
			p.editResponses(choiceA:  'One', choiceB: 'Other')
			p.save(flush: true)
		then:
			p.responses.size() == 3
			p.responses*.value.sort() == ['One', 'Other', 'Unknown']
			p.question == 'A poll question'
	}
}
