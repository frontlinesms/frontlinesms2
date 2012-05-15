package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(PollResponse)
class PollResponseSpec extends Specification {
	def 'PollResponse must have a response value and a poll'() {
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

