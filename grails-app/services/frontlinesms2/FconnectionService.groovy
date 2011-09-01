package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

class FconnectionService {
	def camelContext
	def camelRouteBuilder = new RouteBuilder() {
		@Override
		void configure() {}
		List getRouteDefinitions(Fconnection c) {
			def incoming
			def routes = []
			if(c instanceof SmslibFconnection) {
				incoming = 'seda:raw-smslib'
				routes << from("seda:out-${c.id}")
						.beanRef('smslibTranslationService', 'toCmessage')
						.to(c.camelProducerAddress)
						.routeId("out-${c.id}")
			} else if(c instanceof EmailFconnection) {
				incoming = 'seda:raw-email'
				routes << from('seda:email-messages-to-send')
						.to(c.camelProducerAddress)
						.routeId("out-${c.id}")
			} else if(grails.util.Environment.current == grails.util.Environment.TEST && c instanceof Fconnection) {
				incoming = 'stream:out'
				routes << from('seda:nowhere')
						.to(c.camelProducerAddress)
						.routeId("out-${c.id}")
			} else {
				throw new IllegalStateException("Do not know how to create routes for Fconnection of class: ${c?.class}")
			}
			println "In comes from: $incoming"
			if(incoming && c.camelConsumerAddress) {
				println "from(${c.camelConsumerAddress}).to($incoming).routeId(in-${c.id})"
				routes << from(c.camelConsumerAddress).to(incoming).routeId("in-${c.id}")
			} else {
				println "not creating incoming route: from(${c.camelConsumerAddress}).to($incoming).routeId(in-${c.id})"
			}
			println "Routes created: $routes"
			getLog().info "Creating routes: $routes..."
			routes
		}
	}
	
	def createRoutes(Fconnection c) { // FIXME should be called createRoutes <- plural
		def routes = camelRouteBuilder.getRouteDefinitions(c)
		camelContext.addRouteDefinitions(routes)
	}
	
	def getRouteStatus(Fconnection c) {
		(camelContext.getRoute("in-${c.id}") || camelContext.getRoute("out-${c.id}")) ? RouteStatus.CONNECTED : RouteStatus.NOT_CONNECTED 
	}
}
