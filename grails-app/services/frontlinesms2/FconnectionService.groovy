package frontlinesms2

import org.apache.camel.builder.RouteBuilder

class FconnectionService {
	def camelContext
	def camelRouteBuilder = new RouteBuilder() {
		void configure() {}
		List getRouteDefinitions(Fconnection c) {
			[ from(c.camelAddress).to('seda:raw-email') ]
		}
	}
	
	def createRoute(Fconnection c) {
		camelContext.addRouteDefinitions(camelRouteBuilder.getRouteDefinitions(c))
	}
}
