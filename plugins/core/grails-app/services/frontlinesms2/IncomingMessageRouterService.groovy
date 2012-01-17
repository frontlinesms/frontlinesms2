package frontlinesms2

import java.util.List;

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import org.apache.camel.*

class IncomingMessageRouterService {
	def camelContext
	def camelRouteBuilder = new RouteBuilder() {
		@Override
		void configure() {}
		List getRouteDefinitions() {
			def routes = []
			routes = filter(camelContext.routes, { it.endpoint.endpointUri.contains('fmessages-to-process') })
			getLog().info "Creating routes: $routes..."
			routes
		}
	}
	
	def getRoutes() {
		camelRouteBuilder.getRouteDefinitions()
	}
	
	def Endpoint[] route(String body) {
		def endPoints = routes*.endpoint
		return endPoints
	}
	
	def filter(List l, Closure c) {
		def r = []
		l.each {
			if(c(it)) r << it
		}
		r
	}
}