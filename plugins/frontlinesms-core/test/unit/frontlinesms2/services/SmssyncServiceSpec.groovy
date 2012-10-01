package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*

@TestFor(SmssyncService)
class SmssyncServiceSpec extends Specification {
	private static final boolean TODO = false
	private static final def TEST_MESSAGE = [to:'123', message:'hi']

	def connection
	def requestedId
	def controller
	def camelSentMessage
	def rendered

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
	}

	def setupDefaultConnection() {
		connection.receiveEnabled >> true
		connection.sendEnabled >> true
		connection.outgoingQueueIds >> ([1, 2, 3] as Long[])
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
			def mockDispatch = new Dispatch()
		when:
			service.processSend(mockExchange(mockDispatch))
		then:
			1 * connection.addToQueue(mockDispatch)
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
	def "generateApiResponse with #dispatches in send mode? #sendMode; receiveEnabled? #receiveEnabled; sendEnabled? #sendEnabled"() {
		given:
			connection.receiveEnabled >> receiveEnabled
			connection.sendEnabled >> sendEnabled
			connection.secret >> secret
			controller.params.secret = secret
			if(sendMode) controller.params.task = 'send'
			if(dispatches) connection.outgoingQueueIds >> ((1..dispatches).collect { it } as Long[])
		when:
			def actualResponse = service.generateApiResponse(connection, controller)
		then:
			actualResponse == expectedResponse
		where:
			sendMode | secret | receiveEnabled | sendEnabled | dispatches | expectedResponse
			false    | null   | false          | false       | 0          | [payload:[success:'false']]
			false    | null   | true           | false       | 0          | [payload:[success:'true']]
			false    | 'aa'   | true           | false       | 0          | [payload:[success:'true', secret:'aa']]
			false    | null   | true           | false       | 3          | [payload:[success:'true']]
			false    | null   | true           | true        | 3          | [payload:[success:'true', task:'send', messages:testMessageList(3)]]
			false    | null   | false          | true        | 3          | [payload:[success:'false']]
			true     | null   | false          | true        | 3          | [payload:[success:'true', task:'send', messages:testMessageList(3)]]
			true     | null   | false          | true        | 0          | [payload:[success:'true', task:'send', messages:[]]]
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
}

