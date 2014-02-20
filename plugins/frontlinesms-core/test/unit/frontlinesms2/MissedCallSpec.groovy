package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(MissedCall)
class MissedCallSpec extends Specification {
	def 'calls are unread, inbound and unstarred by default'() {
		when:
			MissedCall missedCall = new MissedCall(src: '123')
		then:
			missedCall.read == false
			missedCall.starred == false
			missedCall.inbound
	}

	def "MissedCall must have a src"() {
		when:
			def m = new MissedCall()
		then:
			!m.validate()
		when:
			def t = new MissedCall(src: 'src')
		then:
			t.validate()
	}
}

