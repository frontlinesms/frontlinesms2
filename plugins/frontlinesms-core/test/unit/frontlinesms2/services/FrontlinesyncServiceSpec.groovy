package frontlinesms2.services

import spock.lang.*

import frontlinesms2.*

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import grails.buildtestdata.mixin.Build

@TestFor(FrontlinesyncService)
@Build(FrontlinesyncFconnection)
@Mock(Dispatch)
class FrontlinesyncServiceSpec extends Specification {
	def connection
	def controller
	def json
	def rendered
	def queue
	def successJson = ([success:true] as grails.converters.JSON).toString(false)
	def sendMessageAndHeadersInvokationCount
	def dispatches
	def setup() {
		rendered = null
		sendMessageAndHeadersInvokationCount = 0
		service.metaClass.sendMessageAndHeaders = { q, b, h ->
			sendMessageAndHeadersInvokationCount++
			queue = q
		}
		dispatches = [
			[id:1, dst:123, status:DispatchStatus.PENDING],
			[id:2, dst:456, status:DispatchStatus.PENDING]
		]
		dispatches.each { it.save = { Map args -> return true } }
		Dispatch.metaClass.static.get = { Serializable id ->
			dispatches.find { it.id == id }
		}
	}

	def 'apiProcess returns 403 if secret is wrong'() {
		given:
			setupConnection('thesecret')
			setupPayload('wrongSecret')
		when:
			service.apiProcess(connection, controller)	
		then:
			rendered.status == 403
	}

	def 'apiProcess returns 403 if secret is missing'() {
		given:
			setupConnection('thesecret')
			setupPayload()
		when:
			service.apiProcess(connection, controller)	
		then:
			rendered.status == 403
	}

	def 'apiProcess does not return 403 if secret is missing but connection has empty secret'() {
		given:
			setupConnection('')
			setupPayload('secret', [
					'missedCalls': [
					[fromNumber: 123, callTimestamp: 1233212341],	
					[fromNumber: 234, callTimestamp: 1524621462],	
					[fromNumber: 345, callTimestamp: 1426142612]
				]
			])
		when:
			service.apiProcess(connection, controller)	
		then:
			rendered.text.toString(false) ==successJson
	}

	def 'each missed call in payload is passed to incoming-missedcalls-to-store'() {
		given:
			setupConnection('secret')
			setupPayload('secret', [
					'missedCalls': [
					[fromNumber: 123, callTimestamp: 1233212341],	
					[fromNumber: 234, callTimestamp: 1524621462],	
					[fromNumber: 345, callTimestamp: 1426142612]
				]
			])
		when:
			service.apiProcess(connection, controller)
		then:
			rendered.text.toString(false) == successJson
			sendMessageAndHeadersInvokationCount == 3
			queue == 'seda:incoming-missedcalls-to-store'
	}

	def 'each text message in payload is passed to incoming-fmessages-to-store'() {
		given:
			setupConnection('secret')
			setupPayload('secret', [
					'inboundTextMessages' : [
					[fromNumber: 123, smsTimestamp: 123123123, text: 'message'],
					[fromNumber: 123, smsTimestamp: 123123123, text: 'message']
					]
				])
		when:
			service.apiProcess(connection, controller)
		then:
			rendered.text.toString(false) == successJson
			sendMessageAndHeadersInvokationCount == 2
			queue == 'seda:incoming-fmessages-to-store'
	}

	def 'each outbound text message status update in the payload results in an update of the corresponding dispatch status'() {
		given:
			setupConnection('secret')
			setupPayload('secret', [
					'outboundTextMessageStatuses' : [
						[dispatchId: 1, deliveryStatus: '2'], // 2 == success
						[dispatchId: 2, deliveryStatus: '9']
					]
				])
		when:
			service.apiProcess(connection, controller)
		then:
			rendered.text.toString(false) == successJson
			dispatches.find { it.id == 1 }.status == DispatchStatus.SENT
			dispatches.find { it.id == 2 }.status == DispatchStatus.FAILED
	}

	def 'apiProcess should return outgoing message payload if available'() {
		given:
			def connection = setupConnection("secret")
			connection.sendEnabled = true
			setupPayload('secret', ['inboundTextMessages':[]])
			service.metaClass.generateOutgoingResponse = { c ->
				['messages':[]]
			}
		when:
			service.apiProcess(connection, controller)
		then:
			rendered.text.toString() == (['messages' : []] as grails.converters.JSON).toString(false)
	}

	def 'generateOutgoingResponses should return messages as a map, including to, message and dispatchId'() {
		given:
			def connection  = setupConnection("secret")
			connection.sendEnabled =  true
			connection.queuedDispatches = [mockDispatch('123', 'yeah', 123)]
			connection.removeDispatchesFromQueue = { q -> true }
		expect:
			service.generateOutgoingResponse(connection).equals([messages:[[to:'123', message:'yeah', dispatchId: 123]]])
			
	}

	def 'processSend should add queuedDispatches to connection'() {
		given:
			def addedToQueuedDispatches = false
			def connection = FrontlinesyncFconnection.build()
			connection.metaClass.addToQueuedDispatches = { d -> addedToQueuedDispatches = true }
			FrontlinesyncFconnection.metaClass.static.get = { id -> connection }
			def x = Mock(Exchange)
			def m = Mock(Message)
			m.headers >> ['fconnection-id':1]
			x.in >> m
		when:
			service.processSend(x)
		then:
			assert addedToQueuedDispatches
			
	}

	private def setupConnection(secret) {
		connection = [id:123, secret:secret]
	}

	private def setupPayload(secret=null, payload=null) {
		json = [secret: secret]
		if(payload) {
			json.payload = payload
		}
		controller = [request:[JSON: json], render: { Map it -> rendered = it }]
	}

	private mockDispatch(dst, text, id) {
		[dst:dst, text:text, id: id, save: { m -> true }]
	}
}
