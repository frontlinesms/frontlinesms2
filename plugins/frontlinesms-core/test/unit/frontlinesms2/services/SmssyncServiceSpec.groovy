package frontlinesms2.services

import org.quartz.Trigger
import frontlinesms2.*
import spock.lang.*

@TestFor(SmssyncService)
class SmssyncServiceSpec extends Specification {
	private static final def TEST_MESSAGE = [to:'123', message:'hi']

	def connection
	def requestedId
	def controller
	def camelSentMessage
	def rendered
	def unscheduleInvokationCount
	def scheduleInvokationCount

	def setup() {
		controller = [params:[:], render:{ rendered = (it as String) }]
		connection = Mock(SmssyncFconnection)

		SmssyncFconnection.metaClass.static.get = { Serializable id -> requestedId = id; connection }
		service.metaClass.sendMessageAndHeaders = { endpoint, body, headers -> camelSentMessage = [endpoint:endpoint, body:body, headers:headers] }
		Dispatch.metaClass.static.getAll = {
			it.collect {
				def d = [id:it, dst:'123'] as Dispatch
				d.expressionProcessorService = [process:{'hi'}]
				return d
			}
		}
		Dispatch.metaClass.save = { Map params -> }
		unscheduleInvokationCount = 0
		scheduleInvokationCount = 0
		ReportSmssyncTimeoutJob.metaClass.static.unschedule = { String name, String group ->
			println "unscheduling $name, $group"	
			unscheduleInvokationCount ++
		}
		ReportSmssyncTimeoutJob.metaClass.static.schedule = { Trigger trigger ->
			println "scheduling $trigger"
			scheduleInvokationCount ++
		}
	}

	def setupDefaultConnection(boolean sendEnabled=true) {
		connection.receiveEnabled >> true
		connection.enabled >> true
		connection.hasDispatches >> true
		if(sendEnabled) {
			connection.sendEnabled >> true
			mockDispatchQueue(connection, [1, 2, 3])
		}
	}

	def 'processSend should get fconnection from exchange headers'() {
		given:
			setupDefaultConnection()
		when:
			service.processSend(mockExchange(null, ['fconnection-id':'123']))
		then:
			requestedId == '123'
	}

	def 'processSend should add exchange body to connection\'s queue'() {
		given:
			setupDefaultConnection()
			def mockDispatch = Mock(Dispatch)
		when:
			service.processSend(mockExchange(mockDispatch))
		then:
			1 * connection.addToQueuedDispatches(_)
	}

	def 'processSend should save connection'() {
		given:
			setupDefaultConnection()
		when:
			service.processSend(mockExchange())
		then:
			1 * connection.save([failOnError:true])
	}

	@Unroll
	def "generateApiResponse with #dispatchCount dispatches in send mode? #sendMode; receiveEnabled? #receiveEnabled; sendEnabled? #sendEnabled"() {
		given:
			connection.receiveEnabled >> receiveEnabled
			connection.sendEnabled >> sendEnabled
			connection.secret >> secret
			connection.enabled >> true
			controller.params.secret = secret
			if(sendMode) controller.params.task = 'send'
			else {
				controller.params.from = '1234567890'
				controller.params.message = 'word'
			}
			if(dispatchCount) {
				mockDispatchQueue(connection, (1..dispatchCount))
				connection.hasDispatches >> true
			}
		when:
			def actualResponse = service.generateApiResponse(connection, controller)
		then:
			actualResponse == expectedResponse
		where:
			sendMode | secret | receiveEnabled | sendEnabled | dispatchCount | expectedResponse
			false    | null   | false          | false       | 0             | [payload:[success:'false', error:'Receive not enabled for this connection']]
			false    | null   | true           | false       | 0             | [payload:[success:'true']]
			false    | 'aa'   | true           | false       | 0             | [payload:[success:'true', secret:'aa']]
			false    | null   | true           | false       | 3             | [payload:[success:'true']]
			false    | null   | true           | true        | 3             | [payload:[success:'true', task:'send', messages:testMessageList(3)]]
			false    | null   | false          | true        | 3             | [payload:[success:'false', error:'Receive not enabled for this connection']]
			true     | null   | false          | true        | 3             | [payload:[success:'true', task:'send', messages:testMessageList(3)]]
			true     | null   | false          | true        | 0             | [payload:[success:'true', task:'send', messages:[]]]
	}

	@Unroll
	def "generateApiResponse with incorrect secret will return failure whatever the other request params"() {
		when:
			connection.receiveEnabled >> receiveEnabled
			connection.sendEnabled >> sendEnabled
			connection.secret >> 'super-password'
			if(sendMode) controller.params.task = 'send'
		then:
			service.generateApiResponse(connection, controller) == [payload:[success:"false"]]
		when:
			controller.params.secret = 'wrong'
		then:
			service.generateApiResponse(connection, controller) == [payload:[success:"false"]]
		where:
			sendMode | receiveEnabled | sendEnabled | dispatches
			false    | false          | false       | 0
			false    | true           | false       | 0
			false    | true           | false       | 0
			false    | true           | false       | 3
			false    | true           | true        | 3
			false    | false          | true        | 3
			true     | false          | true        | 3
			true     | false          | true        | 0
	}

	def 'generateApiResponse for incoming message should forward new TextMessage to storage route and reschedule the timeout counter job'() {
		given:
			connection.timeout >> 1
			setupDefaultConnection(false)
			controller.params.from = '12345'
			controller.params.message = 'hi there boris'
			def storageQueue = []
			service.metaClass.sendMessageAndHeaders = { route, body, headers ->
				if(route == 'seda:incoming-fmessages-to-store') storageQueue << body
				else throw new IllegalArgumentException()
			}
		when:
			service.generateApiResponse(connection, controller)
		then:
			storageQueue.size() == 1 && 
				storageQueue[0] instanceof TextMessage &&
				storageQueue[0].inbound &&
				storageQueue[0].src == '12345' &&
				storageQueue[0].text == 'hi there boris'
			unscheduleInvokationCount == 1
			scheduleInvokationCount == 1
	}

	@Unroll
	def 'generateApiResponse for incoming message with missing fields should respond with suitable error message'() {
		given:
			setupDefaultConnection(false)
			controller.params.from = src
			controller.params.message = text
			def storageQueue = []
			service.metaClass.sendMessageAndHeaders = { route, body, headers ->
				if(route == 'seda:incoming-fmessages-to-store') storageQueue << body
				else throw new IllegalArgumentException()
			}
		when:
			def response = service.generateApiResponse(connection, controller)
		then:
			(badRequest &&
				response == [payload:[success:'false', error:'Missing one or both of `from` and `message` parameters']] &&
				storageQueue == []) ||
			(!badRequest &&
				storageQueue.size() == 1)
		where:
			src  | text | badRequest
			null | null | true
			null | 'hi' | true
			''   | null | true
			''   | 'hi' | true
			'123'| null | true
			'123'| ''   | false
			'123'| 'hi' | false
	}

	def "generateApiResponse for disabled connection should respond with connection not enabled error"() {
		when:
			connection.receiveEnabled >> true
		then:
			service.generateApiResponse(connection, controller) == [payload:[success:'false', error:'Connection not enabled']]
	}

	private def testMessageList(times) {
		(1..times).collect { TEST_MESSAGE }
	}

	private def mockExchange(body=null, headers=['fconnection-id':'999']) {
		def x = Mock(org.apache.camel.Exchange)
		def inMessage = Mock(org.apache.camel.Message)
		inMessage.body >> body
		inMessage.headers >> headers
		x.in >> inMessage
		return x
	}

	private def mockDispatchQueue(connection, ids) {
		connection.queuedDispatches >> Dispatch.getAll(ids)
	}
}

