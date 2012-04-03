package routing

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.*

class IncomingSmslibRouteSpec extends CamelIntegrationSpec {
	def fconnectionService
	
	String getTestRouteFrom() { '' }
	String getTestRouteTo() { '' }
	
	def "should translate a CIncomingMessage into a Fmessage then save it then deliver it to KeywordProcessor"() {
		given:
			def mockPortHandler = MockModemUtils.createMockPortHandler(false, [1:'0891534875001040F30414D0537AD91C7683A465B71E0000013020017560400CC7F79B0C6ABFE5EEB4FB0C'])
			// initialise mock serial device with message available
 			MockModemUtils.initialiseMockSerial(['/def/test-modem': new CommPortIdentifier("COM99",
					mockPortHandler)])
			// start route
			def connection = new SmslibFconnection(name:'test connection', port:'/def/test-modem', baud:9600).save(failOnError:true)
			fconnectionService.createRoutes(connection)
			
			// create a poll called GOO with an answer D
			Poll.createPoll(name:'What do you think of goo?', keyword: 'goo', choiceA:'i like goo', choiceB:'goo is ok',
					choiceC:'i have no strong opinion on goo', choiceD:'goo is horrible').save(failOnError:true, flush:true)
		when:	
			// wait for message to be read from mock serial device
			while(mockPortHandler.messages.size() > 0) { Thread.sleep(50) }
			// wait for message to be processed
			Thread.sleep(5000) // TODO must be a neater way of doing this
		then:	
			// assert Fmessage is saved and has expected content
			Fmessage.findAll()*.text == ['Good morning']
			// assert KeywordProcessor is called
			Fmessage.findAll()[0].messageOwner == Poll.findByName('What do you think of goo?')
			PollResponse.findByValue('goo is horrible').messages == Fmessage.findAll()
		cleanup:	
			// stop route
			if(connection) fconnectionService.destroyRoutes(connection)
			// remove mock serial port
			MockSerial.reset()
	}
}
