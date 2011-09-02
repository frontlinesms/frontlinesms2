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
			def mockPortHandler = MockModemUtils.createMockPortHandler([1:'0891534875001040F30414D0537AD91C7683A465B71E0000013020017560400CC7F79B0C6ABFE5EEB4FB0C'])
			// initialise mock serial device with message available
 			MockModemUtils.initialiseMockSerial(['/def/test-modem': new CommPortIdentifier("COM99",
					mockPortHandler)])
			// start route
			def connection = new SmslibFconnection(name:'test connection', port:'/def/test-modem', baud:9600).save(failOnError:true)
			fconnectionService.createRoutes(connection)
		when:	
			// wait for message to be read from mock serial device
			while(mockPortHandler.messages.size() > 0) { Thread.sleep(50) }
			// wait for message to be processed
			Thread.sleep(5000) // TODO must be a neater way of doing this
		then:	
			// assert Fmessage is saved and has expected content
			Fmessage.findAll()*.text == ['Good morning']
			// TODO assert KeywordProcessor is called
		cleanup:	
			// stop route
			if(connection) fconnectionService.destroyRoutes(connection)
			// remove mock serial port
			MockSerial.reset()
	}
}