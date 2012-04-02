package routing

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.*

class OutgoingSmslibRouteSpec extends CamelIntegrationSpec {
	def messageSendService

	def 'message send service should send a message to connected SMSLib connection'() {
		given:
			// TODO modem is mocked
			// TODO route is setup
			// TODO route is started
			fconnectionService.createRoutes(connection)

			// TODO message is initialised
			def m = new Fmessage()
		when:
			messageSendService.send(m)
		then:
			// TODO check appropriate params have been passed to the mocked modem
			false
	}
}

