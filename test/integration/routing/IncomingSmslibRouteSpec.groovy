package routing

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.*

class IncomingSmslibRouteSpec extends CamelIntegrationSpec {
	String getFrom() { '' }
	String getTo() { '' }
	
	def "should translate a CIncomingMessage into a Fmessage then save it then deliver it to KeywordProcessor"() {
		given:
			// initialise mock serial device with message available
 			MockModemUtils.initialiseMockSerial(['/def/test-modem': new CommPortIdentifier("COM99",
					MockModemUtils.createMockPortHandler([1:'0123456789abcdef'])) /* TODO insert proper PDU payload */ ])
			def c = 0
		when:
			// TODO wait for message to be read from mock serial device
			++c
		then:
			// TODO assert Fmessage is saved
			// TODO assert KeywordProcessor is called
			false
		cleanup:
			// TODO stop mock serial connection
			// TODO remove mock serial port
			c = false
	}
}