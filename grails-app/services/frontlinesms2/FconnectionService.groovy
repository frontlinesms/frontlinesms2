package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

class FconnectionService {
	def camelContext
	def camelRouteBuilder = new RouteBuilder() {
		@Override
		void configure() {}
		List getRouteDefinitions(Fconnection c) {
			[ from(c.camelAddress()).to('seda:raw-email').routeId("${c.id}") ]
		}
	}
	
	def createRoute(Fconnection c) {
		def routes = camelRouteBuilder.getRouteDefinitions(c)
		println "routes: ${routes*.id}"
		camelContext.addRouteDefinitions(routes)
	}
	
	def getRouteStatus(Fconnection c) {
		camelContext.getRoute(c.id?.toString()) ? RouteStatus.CONNECTED : RouteStatus.NOT_CONNECTED 
	}
}
