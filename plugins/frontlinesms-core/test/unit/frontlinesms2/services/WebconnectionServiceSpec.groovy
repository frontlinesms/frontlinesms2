package frontlinesms2.services

import frontlinesms2.*

import spock.lang.*

import org.apache.camel.Exchange
import org.apache.camel.Message

@TestFor(WebconnectionService)
class WebconnectionServiceSpec extends Specification {
	def requestedId
	def mockConnection

	def setup() {
		mockConnection = Mock(Webconnection)
		Webconnection.metaClass.static.get = { Serializable id ->
			requestedId = id
			return mockConnection
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
			def x = mockExchange(null, ['webconnection-id':'123'])
		when:
			service.postProcess(x)
		then:
			requestedId == '123'
			1 * mockConnection.postProcess(x)
	}

	def 'apiProcess should reject a send request without message or recipients'() {
		given:
			def controller = Mock(ApiController)
			controller.request >> [JSON:requestBody]
		when:
			def responseBody = service.apiProcess(controller)
		then:
			1 * controller.sendError(400)
			responseBody == expectedResponse
		where:
			requestBody                         | expectedResponse
			[]                                  | [reason:'missing required field(s): message, recipients']
			[message:'asdf']                    | [reason:'missing required field(s): recipients']
			[recipients:[[type:'group', id:1]]] | [reason:'missing required field(s): message']
			[message:'asdf', recipients:[]]     | [reason:'no recipients supplied']
	}

	def 'apiProcess should trigger messages to groups referenced by ID or name'() {
		given:
			testGroups = [Group.build(name:'a'), Group.build(name:'b')]
			def controller = Mock(ApiController)
			controller.request >> [JSON:requestBody]
			def messageSendService = Mock(MessageSendService)
			def m = Mock(Fmessage)
			messageSendService.createOutgoingMessage(_) >> m
			service.messageSendService = messageSendService
		when:
			service.apiProcess(controller)
		then:
			1 * messageSendService.createOutgoingMessage([groups:testGroups])
			1 * messageSendService.send(m)
		where:
			requestBody << [[[type:'group', id:1], [type:'group', id:2]],
					[[type:'group', name:'a'], [type:'group', name:'b']]]
	}

	def 'apiProcess should trigger messages to smartGroups referenced by ID or name'() {
		given:
			testGroups = [SmartGroup.build(name:'a'), SmartGroup.build(name:'b')]
			def controller = Mock(ApiController)
			controller.request >> [JSON:requestBody]
			def messageSendService = Mock(MessageSendService)
			def m = Mock(Fmessage)
			messageSendService.createOutgoingMessage(_) >> m
			service.messageSendService = messageSendService
		when:
			service.apiProcess(controller)
		then:
			1 * messageSendService.createOutgoingMessage([groups:testGroups])
			1 * messageSendService.send(m)
		where:
			requestBody << [[[type:'smartgroup', id:1], [type:'smartgroup', id:2]],
					[[type:'smartgroup', name:'a'], [type:'smartgroup', name:'b']]]
	}

	def 'apiProcess should trigger messages to contacts referenced by ID or name'() {
		given:
			testContacts = [Contact.build(name:'a'), Contact.build(name:'b')]
			def controller = Mock(ApiController)
			controller.request >> [JSON:requestBody]
			def messageSendService = Mock(MessageSendService)
			def m = Mock(Fmessage)
			messageSendService.createOutgoingMessage(_) >> m
			service.messageSendService = messageSendService
		when:
			service.apiProcess(controller)
		then:
			1 * messageSendService.createOutgoingMessage([contacts:testContacts])
			1 * messageSendService.send(m)
		where:
			requestBody << [[[type:'contact', id:1], [type:'contact', id:2]],
					[[type:'contact', name:'a'], [type:'contact', name:'b']]]
	}

	def 'apiProcess should trigger messages to explicitly listed addresses'() {
		given:
			def controller = Mock(ApiController)
			controller.request >> [JSON:requestBody]
			def messageSendService = Mock(MessageSendService)
			def m = Mock(Fmessage)
			messageSendService.createOutgoingMessage(_) >> m
			service.messageSendService = messageSendService
		when:
			service.apiProcess(controller)
		then:
			1 * messageSendService.createOutgoingMessage([addresses:expectedAddresses])
			1 * messageSendService.send(m)
		where:
			requestBody                                                           | expectedAddresses
			[[type:'address', value:'+123457890']]                                | ['+123457890']
			[[type:'address', value:'+123457890'], [type:'address', value:'213']] | ['+123457890', '213']
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
