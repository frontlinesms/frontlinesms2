package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class DispatchSpec extends UnitSpec {
	def "Dispatch must have a dst, an Fmessage, and a status"() {
		setup:
			mockForConstraintsTests(Dispatch)
			mockForConstraintsTests(Fmessage)
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
			dis.fmessage = new Fmessage()
		then:
			dis.validate()
	}
	
	def "dispatch only has a dateSent if the status is SENT"() {
		setup:
			mockForConstraintsTests(Dispatch)
			mockForConstraintsTests(Fmessage)
		when:
			def now = new Date()
			Dispatch dis = new Dispatch(dst: '12345', fmessage: new Fmessage(), status: DispatchStatus.FAILED, dateSent: now)
		then:
			!dis.validate()
		when:
			dis.status = DispatchStatus.SENT
		then:
			dis.dateSent == now
			println dis.status
			dis.validate()
	}
}

