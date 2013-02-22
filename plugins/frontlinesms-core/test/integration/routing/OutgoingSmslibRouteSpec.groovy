package routing

import static routing.RoutingSpecUtils.*

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.*

import spock.lang.*

class OutgoingSmslibRouteSpec extends grails.plugin.spock.IntegrationSpec {
	def messageSendService
	def fconnectionService
	def appSettingsService
	static transactional = false

	def 'message send service should send a message to connected SMSLib connection'() {
		given:
			def sentMessages = []
			// modem is mocked
			MockModemUtils.initialiseMockSerial(MOCK1:new CommPortIdentifier('MOCK1',
					MockModemUtils.createMockPortHandler(true, [:], sentMessages)))
			// route is setup and started
			def connection = new SmslibFconnection(name:'test connection', port:'MOCK1', baud:9600).save(failOnError:true, flush:true)
			fconnectionService.createRoutes(connection)
			appSettingsService.set('routing.use', "fconnection-${connection.id}")
			// message is initialised
			def m = createOutgoing('+447890123456', 'test message')
		when:
			messageSendService.send(m)
		then:
			// check appropriate params have been passed to the mocked modem
			waitFor { sentMessages == ['0011000C914487092143650000FF0CF4F29C0E6A97E7F3F0B90C'] }
		cleanup:
			Fmessage.withNewSession {
				Fmessage.findAll().each {
					it.refresh()
					it.delete()
				}
			}
	}
}

