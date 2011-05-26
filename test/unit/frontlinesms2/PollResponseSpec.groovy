package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class PollResponseSpec extends UnitSpec {

	def 'PollResponse must have a response value'() {
		given:
			mockForConstraintsTests(PollResponse)
		when:
			def noResponse = new PollResponse(poll:new Poll())
		then:
			!noResponse.validate()
		when:
			noResponse.value = 'something'
		then:
			noResponse.validate()
	}

	def 'PollResponse may have none, one or many messages associated with it'() {
		given:
			mockDomain(PollResponse)
		when:
			def r = new PollResponse(value:'yes', poll:new Poll())
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

	def "Adding a message to a PollResponse will cascade to the message's activity value"() {
		// FIXME this almost certainly needs to be an integration test due to reliance on cascades (cascades are probably enforced by Hibernate rather than GORM)
		given:
			mockDomain(Poll)
			mockDomain(Fmessage)
			mockDomain(PollResponse)
			def r = new PollResponse(value:'yes', poll:new Poll()).save()
			def m = new Fmessage()
		when:
			r.addToMessages(m)
			r.save()
		then:
			m.activity == r
	}
}

