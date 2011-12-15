package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class PollResponseSpec extends UnitSpec {

	def 'PollResponse must have a response value and a poll'() {
		given:
			mockForConstraintsTests(PollResponse)
		when:
			def noResponse = new PollResponse()
		then:
			!noResponse.validate()
		when:
			noResponse.value = 'something'
			noResponse.poll = new Poll()
		then:
			noResponse.validate()
	}

	def 'PollResponse may have none, one or many messages associated with it'() {
		given:
			mockDomain(PollResponse)
		when:
			def r = new PollResponse(value:'yes', poll: new Poll())
		then:
			r.validate()
		when:
			r.addToMessages(new Fmessage())
		then:
			r.validate()
		when:
			r.addToMessages(new Fmessage())
		then:
			r.validate()
	}
}

