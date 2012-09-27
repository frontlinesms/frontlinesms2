package frontlinesms2

import spock.lang.*

import org.apache.camel.Exchange
import org.apache.camel.Message

@TestFor(WebConnectionService)
class WebConnectionServiceSpec extends Specification {
	def requestedId
	def mockConnection

	def setup() {
		mockConnection = Mock(WebConnection)
		WebConnection.metaClass.static.get = { id ->
			requestedId = id
			return mockConnection
		}
	}

	// TODO: this should result in a merge conflict when merging with CrowdhahidiSMS Crackathon branch
	// Crowdahidi side is right, this side is wrong.
	
	// def 'preprocess call is handed back to the relevant domain object'() {
	// 	given:
	// 		def x = mockExchange(null, ['webconnection-id':'123'])
	// 	when:
	// 		service.preProcess(x)
	// 	then:
	// 		requestedId == '123'
	// 		1 * mockConnection.preProcess(x)
	// }

	// def 'postprocess call is handed back to the relevant domain object'() {
	// 	given:
	// 		def x = mockExchange(null, ['webconnection-id':'123'])
	// 	when:
	// 		service.postProcess(x)
	// 	then:
	// 		requestedId == '123'
	// 		1 * mockConnection.postProcess(x)
	// }

	Exchange mockExchange(body, Map headers) {
		Exchange x = Mock()
		def inMessage = Mock(Message)
		if(body) inMessage.body >> body
		inMessage.headers >> headers
		x.in >> inMessage
		return x
	}
}
