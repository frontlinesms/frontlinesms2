package frontlinesms2.services

import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*

import org.apache.camel.CamelContext
import org.apache.camel.Endpoint
import org.apache.camel.Route

@TestFor(IncomingMessageRouterService)
class IncomingMessageRouterServiceSpec extends Specification {
	def camelContext
	
	def setup() {
		camelContext = Mock(CamelContext)
		service.camelContext = camelContext
	}
	
	def "should route to all endpoints ending with -fmessages-to-process"() {
		given:
			camelContext.getRoutes() >> mockRoutes(
					'dispatch-route',
					'outgoing-fmessages',
					'incoming-fmessages-to-process',
					'radio-fmessages-to-process',
					'incoming-fmessages-to-discard')
			println "camelContext.getRoutes() : $camelContext.routes"
		when:
			def endpoints = service.route(null)
		then:
			endpoints*.endpointUri == [
					'incoming-fmessages-to-process',
					'radio-fmessages-to-process']
	}
	
	def mockRoutes(String... routeNames) {
		routeNames.collect { routeName ->
			Endpoint e = Mock()
			e.endpointUri >> routeName
			Route r = Mock()
			r.endpoint >> e
			r
		}
	}
}
