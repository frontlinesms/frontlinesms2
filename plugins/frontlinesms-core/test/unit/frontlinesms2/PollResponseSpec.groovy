package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(PollResponse)
class PollResponseSpec extends Specification {
	def 'PollResponse must have a response value and a poll and a key'() {
		expect:
			new PollResponse(poll:poll, key:key, value:value).validate() == valid
		where:
			valid | key  | value       | poll
			false | null | null        | null
			false | null | null        | new Poll()
			false | null | 'something' | null
			false | null | 'something' | new Poll()
			false | 'A'  | null        | null
			false | 'A'  | 'something' | null
			false | 'A'  | null        | new Poll()
			true  | 'A'  | 'something' | new Poll()
	}

	def 'PollResponse may have none, one or many messages associated with it'() {
		when:
			def r = new PollResponse(value:'yes', poll: new Poll(), key:'A')
		then:
			r.validate()
		when:
			r.addToMessages(new TextMessage())
		then:
			r.validate()
		when:
			r.addToMessages(new TextMessage())
		then:
			r.validate()
	}
}

