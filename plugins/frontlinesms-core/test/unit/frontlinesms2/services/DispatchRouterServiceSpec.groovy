package frontlinesms2.services

import frontlinesms2.*
import frontlinesms2.camel.exception.NoRouteAvailableException

import spock.lang.*
import grails.test.mixin.*

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message

@TestFor(DispatchRouterService)
@Mock([Dispatch, Fmessage, Fconnection, SmslibFconnection, SystemNotification])
class DispatchRouterServiceSpec extends Specification {
	def appSettingsService

	def setup() {
		Fmessage.metaClass.static.findBySrc = { src, map->
			def m = Mock(Fmessage)
			def f = Mock(Fconnection)
			f.id >> 2
			m.receivedOn >> f
			return m
		}

		Dispatch.metaClass.static.get = { id->
			def d = Mock(Dispatch)
			d.id >> id
			d.dst >> '123456'
			println " mocked dispatch $d"
			return d
		}

		service.i18nUtilService = Mock(I18nUtilService)
		service.i18nUtilService.getMessage(_) >> 'blah blah blah'

		appSettingsService = Mock(AppSettingsService)
		service.appSettingsService = appSettingsService
	}

	def "should update the dispatch when no route is found"() {
		setup:
			def exchange = Mock(Exchange)
			def camelContext = Mock(CamelContext)
			camelContext.getRoutes() >> []

			def camelMessage = Mock(org.apache.camel.Message)
			exchange.getIn() >> camelMessage
			camelMessage.getBody() >> new Dispatch(dst:"dst", message:new Fmessage())

			service.camelContext = camelContext
		when:
			service.slip(exchange, null, null)
		then:
			thrown NoRouteAvailableException
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

	def 'slip should assign message to the last received route if route preference set to last received route'() {
		given:
			mockAppSettingsService(true, 'any')
			mockRoutes(1, 2, 3)
		when:
			def routedTo = service.slip(mockExchange(), null, null)
		then:
			routedTo == "seda:out-2"
	}

	@Unroll
	def 'slip should use the defined rules to determine fconnection to use'() {
		given:
			mockAppSettingsService(settings)
			def fconnection1 = new SmslibFconnection(name:"test 1", port:"/dev/ttyUSB0").save(flush:true)
			def fconnection2 = new SmslibFconnection(name:"test 2", port:"/dev/ttyUSB0").save(flush:true)
			def fconnection3 = new SmslibFconnection(name:"test 3", port:"/dev/ttyUSB0").save(flush:true)
			mockRoutes(fconnection1.id.toInteger(), fconnection2.id.toInteger(), fconnection3.id.toInteger())
		expect:
			service.slip(mockExchange(), null, null) == route
		where:
			settings                                                       | route 
			[true, 'any', 'fconnection-4, fconnection-1, fconnection-2']   | "seda:out-1"
			[true, 'any', 'uselastreceiver, fconnection-3, fconnection-1'] | "seda:out-2"
	}

	def 'slip should not assign messages to any route if routing preference is not to send messages even if routes are available'() {
		given:
			mockAppSettingsService(false, 'dontsend')
			mockRoutes(1, 2, 3)
		when:
			service.slip(mockExchange(), null, null)
		then:
			thrown NoRouteAvailableException
	}

	def 'slip should not assign messages to any route if routing preference is not to send messages when routes are not avalilable'() {
		given:
			mockAppSettingsService(false, 'dontsend')
		when:
			service.slip(mockExchange(), null, null)
		then:
			thrown NoRouteAvailableException
	}

	private def mockExchange() {
		def exchange = Mock(Exchange)
		exchange.in >> mockExchangeMessage(['frontlinesms.dispatch.id':'1'], Mock(Dispatch))
		exchange.out >> mockExchangeMessage([:], null)
		println "x.in.headers ######### $exchange.in.headers"
		return exchange
	}
	
	private mockExchangeMessage(headers, body){
		def m = Mock(Message)
		body.id >> 1

		m.body >> body
		m.headers >> headers
		return m
	}
	private def mockRoutes(int... ids) {
		CamelContext c = Mock()
		c.routes >> ids.collect { [[id:"in-$it"], [id:"out-$it"]] }.flatten()
		service.camelContext = c
	}
	
	private def mockRoutes(Map idsAndPrefixes) {
		CamelContext c = Mock()
		c.routes >> idsAndPrefixes.collect { k, v -> [[id:"in-$k"], [id:"out-$v-$k"]] }.flatten()
		service.camelContext = c
	}

	private mockAppSettingsService(useLastReceiver, otherwise, use = null) {
		if(useLastReceiver) use = use? "$use,uselastreceiver": 'uselastreceiver'
		appSettingsService.get("routing.use") >> use
		appSettingsService.get("routing.otherwise") >> otherwise
		service.appSettingsService = appSettingsService
	}
}

