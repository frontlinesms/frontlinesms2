package frontlinesms2.services

import frontlinesms2.*

import spock.lang.*

import org.apache.camel.Exchange
import org.apache.camel.Message

import grails.buildtestdata.mixin.Build

@TestFor(WebconnectionService)
@Build([Group, SmartGroup, Contact, Fmessage])
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
			def m = Fmessage.build()
			def x = mockExchange(null, ['webconnection-id':'123','fmessage-id':m.id])
		when:
			service.postProcess(x)
		then:
			requestedId == '123'
			1 * mockConnection.postProcess(x)
	}

	@Unroll
	def 'apiProcess should reject a send request without the correct secret'() {
		given:
			def webcon = [addToMessages: { println "addToMessages called with args $it" }, 
				save: { println "save called with args $it" }, secret:"secret"]
			def renderedArgs
			def controller = [request:[:], render: { renderedArgs = it }]
			controller.request = [JSON:requestBody]
		when:
			service.apiProcess(webcon, controller)
		then:
			renderedArgs.status == 401
			renderedArgs.text == expectedResponse
		where:
			requestBody                                                           | expectedResponse
			[message:'asdf', recipients:[type:'group', id:1]]                     | 'no secret provided'
			[secret:"wrong", message:'asdf', recipients:[type:'group', id:1]]     | 'invalid secret'
	}

	@Unroll
	def 'apiProcess should reject a send request without message or recipients'() {
		given:
			def webcon = [addToMessages: { println "addToMessages called with args $it" }, 
				save: { println "save called with args $it" }, secret:"secret"]
			def renderedArgs
			def controller = [request:[:], render: { renderedArgs = it }]
			controller.request = [JSON:requestBody]
			def messageSendService = Mock(MessageSendService)
			def m = Mock(Fmessage)
			m.dispatches >> [] // for test with non-existant group
			messageSendService.createOutgoingMessage(_) >> m
			service.messageSendService = messageSendService
		when:
			service.apiProcess(webcon, controller)
		then:
			renderedArgs.status == 400
			renderedArgs.text == expectedResponse
		where:
			requestBody                                                             | expectedResponse
			[secret:"secret"]                                                       | 'missing required field(s): message, recipients'
			[secret:"secret", message:'asdf']                                       | 'missing required field(s): recipients'
			[secret:"secret", recipients:[[type:'group', id:1]]]                    | 'missing required field(s): message'
			[secret:"secret", message:'asdf', recipients:[]]                        | 'no recipients supplied'
			[secret:"secret", message:'asdf', recipients:[[type:'group', id:123]]]  | 'no recipients supplied'
	}

	@Unroll
	def 'apiProcess should trigger messages to groups referenced by ID or name (case insensitive)'() {
		given:
			def webcon = [addToMessages: { println "addToMessages called with args $it" }, 
				save: { println "save called with args $it" }, secret:"secret"]
			def renderedArgs
			def testGroups = [Group.build(name:'a'), Group.build(name:'b')]
			def controller = [request:[:], render: { renderedArgs = it }]
			controller.request = [JSON:requestBody]
			def messageSendService = Mock(MessageSendService)
			def m = Mock(Fmessage)
			m.dispatches >> ['1', '2', '3']
			messageSendService.createOutgoingMessage(_) >> m
			service.messageSendService = messageSendService
		when:
			service.apiProcess(webcon, controller)
		then:
			1 * messageSendService.createOutgoingMessage(_) >> { Map req -> assert req.groups == testGroups; return m }
			1 * messageSendService.send(m)
		where:
			requestBody << [[secret:"secret", message:"this is the message", recipients:[[type:'group', id:1], [type:'group', id:2]]],
					[secret:"secret", message:"this is the message", recipients:[[type:'group', name:'a'], [type:'group', name:'b']]],
					[secret:"secret", message:"this is the message", recipients:[[type:'group', name:'A'], [type:'group', name:'B']]]]
	}

	@Unroll
	def 'apiProcess should trigger messages to smartGroups referenced by ID or name (case insensitive)'() {
		given:
			def webcon = [addToMessages: { println "addToMessages called with args $it" }, 
				save: { println "save called with args $it" }, secret:"secret"]
			def renderedArgs
			def testGroups = [SmartGroup.build(name:'a', mobile:'+44'), SmartGroup.build(name:'b', mobile:'+254')]
			def controller = [request:[:], render: { renderedArgs = it }]
			controller.request = [JSON:requestBody]
			def messageSendService = Mock(MessageSendService)
			def m = Mock(Fmessage)
			m.dispatches >> ['1', '2', '3']
			messageSendService.createOutgoingMessage(_) >> m
			service.messageSendService = messageSendService
		when:
			service.apiProcess(webcon, controller)
		then:
			1 * messageSendService.createOutgoingMessage(_) >> { Map req -> assert req.groups == testGroups; return m }
			1 * messageSendService.send(m)
		where:
			requestBody << [[secret:"secret", message:"this is a message", recipients:[[type:'smartgroup', id:1], [type:'smartgroup', id:2]]],
					[secret:"secret", message:"this is a message", recipients:[[type:'smartgroup', name:'A'], [type:'smartgroup', name:'B']]],
					[secret:"secret", message:"this is a message", recipients:[[type:'smartgroup', name:'a'], [type:'smartgroup', name:'b']]]]
	}

	@Unroll
	def 'apiProcess should trigger messages to contacts referenced by ID or name (case insensitive)'() {
		given:
			def webcon = [addToMessages: { println "addToMessages called with args $it" }, 
				save: { println "save called with args $it" }, secret:"secret"]
			def renderedArgs
			def testContacts = [Contact.build(name:'a', mobile:'12'), Contact.build(name:'b', mobile:'23')]
			def controller = [request:[:], render: { renderedArgs = it }]
			controller.request = [JSON:requestBody]
			def messageSendService = Mock(MessageSendService)
			def m = Mock(Fmessage)
			m.dispatches >> ['1', '2', '3']
			messageSendService.createOutgoingMessage(_) >> m
			service.messageSendService = messageSendService
		when:
			service.apiProcess(webcon, controller)
		then:
			1 * messageSendService.createOutgoingMessage(_) >> { Map req -> assert req.addresses == testContacts*.mobile; return m }
			1 * messageSendService.send(m)
		where:
			requestBody << [[secret:"secret", message:"test", recipients: [[type:'contact', id:1], [type:'contact', id:2]]],
					[secret:"secret", message:"test", recipients: [[type:'contact', name:'A'], [type:'contact', name:'B']]],
					[secret:"secret", message:"test", recipients: [[type:'contact', name:'a'], [type:'contact', name:'b']]]]
	}

	@Unroll
	def 'apiProcess should not expect secret if none is configured in the activity'() {
		given:
			def webcon = [addToMessages: { println "addToMessages called with args $it" }, 
				save: { println "save called with args $it" }, secret:""]
			def renderedArgs
			def testContacts = [Contact.build(name:'a', mobile:'12'), Contact.build(name:'b', mobile:'23')]
			def controller = [request:[:], render: { renderedArgs = it }]
			controller.request = [JSON:requestBody]
			def messageSendService = Mock(MessageSendService)
			def m = Mock(Fmessage)
			m.dispatches >> ['1', '2', '3']
			messageSendService.createOutgoingMessage(_) >> m
			service.messageSendService = messageSendService
		when:
			service.apiProcess(webcon, controller)
		then:
			1 * messageSendService.createOutgoingMessage(_) >> { Map req -> assert req.addresses == testContacts*.mobile; return m }
			1 * messageSendService.send(m)
		where:
			requestBody << [[secret:"", message:"test", recipients: [[type:'contact', id:1], [type:'contact', id:2]]],
					[message:"test", recipients: [[type:'contact', name:'A'], [type:'contact', name:'B']]]]
	}

	@Unroll
	def 'apiProcess should trigger messages to explicitly listed addresses'() {
		given:
			def webcon = [addToMessages: { println "addToMessages called with args $it" }, 
				save: { println "save called with args $it" }, secret:"secret"]
			def renderedArgs
			def controller = [request:[:], render: { renderedArgs = it }]
			controller.request = [JSON:requestBody]
			def messageSendService = Mock(MessageSendService)
			def m = Mock(Fmessage)
			m.dispatches >> ['1', '2', '3']
			messageSendService.createOutgoingMessage(_) >> m
			service.messageSendService = messageSendService
		when:
			service.apiProcess(webcon, controller)
		then:
			1 * messageSendService.createOutgoingMessage(_) >> { Map req -> assert req.addresses == expectedAddresses; return m }
			1 * messageSendService.send(m)
		where:
			requestBody                                                                                         | expectedAddresses
			[secret:"secret", message:"test", recipients: [[type:'address', value:'+123457890']]]                                | ['+123457890']
			[secret:"secret", message:"test", recipients: [[type:'address', value:'+123457890'], [type:'address', value:'213']]] | ['+123457890', '213']
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
