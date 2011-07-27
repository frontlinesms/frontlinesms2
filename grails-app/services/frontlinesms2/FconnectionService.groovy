package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

class FconnectionService {
	def camelContext
	def camelRouteBuilder = new RouteBuilder() {
		@Override
		void configure() {}
		List getRouteDefinitions(Fconnection c) {
			def incoming, outgoing
			def routes = []
			if(c instanceof SmslibFconnection) {
				incoming = 'seda:raw-smslib'
				outgoing = 'seda:smslib-messages-to-send'
			} else if(c instanceof EmailFconnection) {
				incoming = 'seda:raw-email'
				outgoing = 'seda:email-messages-to-send'
			} else if(grails.util.Environment.current == grails.util.Environment.TEST && c instanceof Fconnection) {
				incoming = 'stream:out'
				outgoing = 'seda:nowhere'
			} else {
				throw new IllegalStateException("Do not know how to create routes for Fconnection of class: ${c?.class}")
			}
			getLog().info "Creating routes: $routes..."
			println "In comes from: $incoming"
			if(incoming && c.camelConsumerAddress) {
				println "from(${c.camelConsumerAddress}).to($incoming).routeId(in-${c.id})"
				routes << from(c.camelConsumerAddress).to(incoming).routeId("in-${c.id}")
			} else {
				println "not creating incoming route: from(${c.camelConsumerAddress}).to($incoming).routeId(in-${c.id})"
			}
			println "out goes to: $outgoing"
			if(outgoing && c.camelProducerAddress) {
				println "from($outgoing).to(${c.camelProducerAddress}).routeId(out-${c.id})"
				routes << from(outgoing).to(c.camelProducerAddress).routeId("out-${c.id}")
			} else {
				println "not creating outgoing route: from($outgoing).to(${c.camelProducerAddress}).routeId(out-${c.id})"
			}
			println 'Routes created.'
			routes
		}
	}
	
	def createRoute(Fconnection c) {
		def routes = camelRouteBuilder.getRouteDefinitions(c)
		camelContext.addRouteDefinitions(routes)
	}
	
	def getRouteStatus(Fconnection c) {
		(camelContext.getRoute("in-${c.id}") || camelContext.getRoute("out-${c.id}")) ? RouteStatus.CONNECTED : RouteStatus.NOT_CONNECTED 
	}
}
