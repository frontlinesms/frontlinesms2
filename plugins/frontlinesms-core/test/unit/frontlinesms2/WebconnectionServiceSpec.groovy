package frontlinesms2

import spock.lang.*

import org.apache.camel.*

@TestFor(WebconnectionService)
@Mock([GenericWebconnection, Fmessage])
class WebconnectionServiceSpec extends Specification {
	def requestedId
	def mockConnection

	def setup() {
		mockConnection = Mock(Webconnection)
		Webconnection.metaClass.static.get = { Serializable id ->
			requestedId = id
			return mockConnection
		}
		Fmessage.metaClass.static.get = { Serializable id ->
			return Mock(Fmessage)
		}
	}

	def 'preprocess call is handed back to the relevant domain object'() {
		given:
			def x = mockExchange(null, ['webconnection-id':'123'])
		when:
			service.preProcess(x)
		then:
			requestedId == '123'
			1 * mockConnection.preProcess(x)
	}


	def 'postprocess call is handed back to the relevant domain object'() {
		given:
			def x = mockExchange(null, ['webconnection-id':'123','fmessage-id':'1'])
		when:
			service.postProcess(x)
		then:
			requestedId == '123'
			1 * mockConnection.postProcess(x)
	}

	Exchange mockExchange(body, Map headers) {
		Exchange x = Mock()
		def inMessage = Mock(Message)
		if(body) inMessage.body >> body
		inMessage.headers >> headers
		x.in >> inMessage
		return x
	}
}
