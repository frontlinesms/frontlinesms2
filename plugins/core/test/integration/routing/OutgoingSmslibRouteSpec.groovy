package routing

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.*

import spock.lang.*

class OutgoingSmslibRouteSpec extends grails.plugin.spock.IntegrationSpec {
	def messageSendService
	def fconnectionService

	def 'message send service should send a message to connected SMSLib connection'() {
		given:
			def sentMessages = []
			// modem is mocked
			MockModemUtils.initialiseMockSerial(MOCK1:new CommPortIdentifier('MOCK1',
					MockModemUtils.createMockPortHandler(true, [:], sentMessages)))
			// route is setup and started
			def connection = SmslibFconnection.build(port:'MOCK1').save(failOnError:true)
			fconnectionService.createRoutes(connection)

			// message is initialised
			def m = createOutgoing('+447890123456', 'test message')
		when:
			messageSendService.send(m)
		then:
			// check appropriate params have been passed to the mocked modem
			waitFor { sentMessages == ['0011000C914487092143650000FF0CF4F29C0E6A97E7F3F0B90C'] }
	}

	private Fmessage createOutgoing(String dst, String text) {
		Fmessage m = new Fmessage(text:text, inbound:false, hasPending:true)
		m.addToDispatches(new Dispatch(dst:dst, status:DispatchStatus.PENDING))
		return m
	}

	private def waitFor(Closure c, pause=200, totalWait=5000) {
		for(long end = System.currentTimeMillis() + totalWait; System.currentTimeMillis() < end;) {
			if(c.call()) return true
			else sleep(pause)
		}
		return false
	}
}

