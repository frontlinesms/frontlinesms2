package routing

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.*

class IncomingSmslibRouteSpec extends CamelIntegrationSpec {
	def fconnectionService
	
	String getFrom() { '' }
	String getTo() { '' }
	
	def "should translate a CIncomingMessage into a Fmessage then save it then deliver it to KeywordProcessor"() {
		given:
			def mockPortHandler = MockModemUtils.createMockPortHandler([1:'0123456789abcdef']) /* TODO insert proper PDU payload */
			// initialise mock serial device with message available
 			MockModemUtils.initialiseMockSerial(['/def/test-modem': new CommPortIdentifier("COM99",
					mockPortHandler)])
			// start route
			def connection = new SmslibFconnection('/def/test-modem').save(failOnError:true)
			fconnectionService.createRoutes(connection)
		when:
			// wait for message to be read from mock serial device
			while(mockPortHandler.hasMessages()) { /* wait */ }
		then:
			// assert Fmessage is saved
			Fmessage.findAll().size() == 1
			// TODO check Fmessage content?
			// TODO assert KeywordProcessor is called
		cleanup:
			// stop route
			fconnectionService.destroyRoutes(connection)
			// remove mock serial port
			MockSerial.reset()
	}
}