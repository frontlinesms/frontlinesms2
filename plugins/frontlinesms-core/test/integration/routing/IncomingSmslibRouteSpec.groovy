package routing

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.*

class IncomingSmslibRouteSpec extends grails.plugin.spock.IntegrationSpec {
	static transactional = false
	def fconnectionService
	def sessionFactory
	
	String getTestRouteFrom() { '' }
	String getTestRouteTo() { '' }
	
	def "should translate a CIncomingMessage into a TextMessage then save it then deliver it to KeywordProcessor"() {
		given:
			def mockPortHandler = MockModemUtils.createMockPortHandler(false, [1:'0891534875001040F30414D0537AD91C7683A465B71E0000013020017560400CC7F79B0C6ABFE5EEB4FB0C'])
			// initialise mock serial device with message available
 			MockModemUtils.initialiseMockSerial(['/dev/test-modem': new CommPortIdentifier("COM99",
					mockPortHandler)])
			// start route
			def connection = new SmslibFconnection(name:'test connection', port:'/dev/test-modem', baud:9600).save(failOnError:true)
			fconnectionService.createRoutes(connection)
			
			// create a poll called GOO with an answer D
			createPoll(name:'What do you think of goo?', keyword:'good', choiceA:'i like goo', choiceB:'goo is ok',
					choiceC:'i have no strong opinion on goo', choiceD:'goo is horrible')
		when:
			// wait for message to be read from mock serial device
			while(mockPortHandler.receiveMessages) { sleep(100) }
			sleep 3000
			sessionFactory.currentSession.flush()
			sleep 10000
		then:
			def poll = Poll.findByName('What do you think of goo?')
			def messages = TextMessage.findAll()*.refresh()
			messages.size() == 1
			messages[0].text == 'Good morning'
			messages[0].messageOwner == poll
		cleanup:	
			// stop route
			if(connection) fconnectionService.destroyRoutes(connection)
			// remove mock serial port
			MockSerial.reset()
	}

	def createPoll(attrs) {
		Poll p = new Poll(name:attrs.name)
		if(attrs.keyword) {
			def k = new Keyword(value:attrs.keyword.toUpperCase())
			p.addToKeywords(k)
		}
		p.editResponses(attrs)
		p.save(failOnError:true, flush:true)
		return p
	}
}

