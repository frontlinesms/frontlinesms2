package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(Dispatch)
@Mock(Fmessage)
class DispatchSpec extends Specification {
	def "Dispatch must have a dst, a message, and a status"() {
		when:
			Dispatch dis = new Dispatch()
		then:
			!dis.validate()
		when:
			dis.dst = '123456'
		then:
			!dis.validate()
		when:
			dis.status = DispatchStatus.FAILED
		then:
			!dis.validate()
		when:
			dis.message = new Fmessage()
		then:
			dis.validate()
	}
	
	def "dispatch only has a dateSent if the status is SENT"() {
		when:
			def now = new Date()
			Dispatch dis = new Dispatch(dst: '12345', message: new Fmessage(), status: DispatchStatus.FAILED, dateSent: now)
		then:
			!dis.validate()
		when:
			dis.status = DispatchStatus.SENT
		then:
			dis.dateSent == now
			dis.validate()
	}
}

