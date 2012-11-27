package frontlinesms2.services

import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message

@TestFor(DispatchRouterService)
@Mock([Dispatch, Fmessage])
class DispatchRouterServiceSpec extends Specification {
	def setup() {
		Fmessage.metaClass.static.findBySrcAndOrderByDateCreated = { src->
			def m = Mock(Fmessage)
			m.receivedOn >> '2'
			return m
		}

		Dispatch.metaClass.static.get = { id->
			def d = Mock(Dispatch)
			d.id >> id
			d.dst >> '123456'
			println " mocked dispatch $d"
			return d
		}
	}
	def "should update the dispatch when no route is found"() {
		setup:
			def exchange = Mock(Exchange)
			def camelContext = Mock(CamelContext)
			camelContext.getRoutes()>> []

			def camelMessage = Mock(org.apache.camel.Message)
			exchange.getIn() >> camelMessage
			camelMessage.getBody() >> new Dispatch(dst:"dst", message:new Fmessage())

			service.camelContext = camelContext
		when:
			service.slip(exchange, null, null)
		then:
			RuntimeException ex = thrown()
	}
	
	@Unroll
	def 'slip should return null if previous is set'() {
		given:
			def x = Mock(Exchange)
		expect:
			service.slip(x, previous, null) == null
		where:
			previous << 'seda:out-99'
	}
	
	@Unroll
	def 'slip should route to specified route if fconnection header is set'() {
		given:
			def x = Mock(Exchange)
		expect:
			service.slip(x, null, "$id") == "seda:out-$id"
		where:
			id << [1, 10, 100]
	}

	def 'slip should assign message to the last received route if route preference set to last received route'(){
		given:
			mockAppSettingsService(true,'any')
			mockRoutes(1, 2, 3)
		when:
			def routedTo = service.slip(mockExchange(), null, null)
		then:
			routedTo == "seda:out-2"
	}

	def 'slip should not assign messages to any route and should set message status to failed if routing preference is not to send messages'(){

	}

	def 'slip should fall back to the -otherwise- if received connection is set as prefered route and it is not avalilable'(){

	}

	def 'slip should assign messages to round robin if routing preference is set to use avalilable routes'() {
		given:
			mockAppSettingsService(false,'any')
			mockRoutes(1, 2, 3)
		when:
			def routedTo = (1..5).collect { service.slip(mockExchange(), null, null) }
		then:
			routedTo == [1, 2, 3, 1, 2].collect { "seda:out-$it" }
	}

	def 'slip should prioritise internet services over modems if routing preference is set to use avalilable routes'() {
		given:
			mockAppSettingsService(false,'any')
			mockRoutes(1:'internet', 2:'modem', 3:'internet', 4:'modem')
		when:
			def routedTo = (1..5).collect { service.slip(mockExchange(), null, null) }
		then:
			routedTo == [1, 3, 1, 3, 1].collect { "seda:out-$it" }
	}

	private def mockExchange() {
		def exchange = Mock(Exchange)
		exchange.in >> mockExchangeMessage(['frontlinesms.dispatch.id':'1'], null)
		exchange.out >> mockExchangeMessage([:], null)
		println "x.in.headers ######### $exchange.in.headers"
		return exchange
	}
	
	private mockExchangeMessage(headers, body){
		def m = Mock(Message)
		m.body >> body
		m.headers >> headers
		return m
	}
	private def mockRoutes(int...ids) {
		CamelContext c = Mock()
		c.routes >> ids.collect { [[id:"in-$it"], [id:"out-$it"]] }.flatten()
		service.camelContext = c
	}
	
	private def mockRoutes(Map idsAndPrefixes) {
		CamelContext c = Mock()
		c.routes >> idsAndPrefixes.collect { k, v -> [[id:"in-$k"], [id:"out-$v-$k"]] }.flatten()
		service.camelContext = c
	}

	private mockAppSettingsService($userLastReceived, $otherwise){
		AppSettingsService appSettingsService = Mock()
		appSettingsService.get("routing.uselastreceiver") >> $userLastReceived
		appSettingsService.get("routing.otherwise") >> $otherwise
		service.appSettingsService = appSettingsService
	}
}
