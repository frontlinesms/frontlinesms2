package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

class FconnectionService {
	def camelContext
	def camelRouteBuilder = new RouteBuilder() {
		@Override
		void configure() {}
		List getRouteDefinitions(Fconnection c) {
			def inGoesTo, outComesFrom
			if(c instanceof SmslibFconnection) {
				inGoesTo = 'seda:raw-smslib'
				outComesFrom = 'seda:smslib-messages-to-send'
			} else if(c instanceof EmailFconnection) {
				inGoesTo = 'seda:email-messages-to-send'
				outComesFrom = 'seda:raw-email'
			} else if(grails.util.Environment.current == grails.util.Environment.TEST && c instanceof Fconnection) {
				inGoesTo = 'stream:out'
				outComesFrom = 'seda:nowhere'
			} else {
				throw new IllegalStateException("Do not know how to create routes for Fconnection of class: ${c?.class}")
			}
			def routes = []
			getLog().info "Creating routes: $routes..."
			println "In goes to: $inGoesTo"
			println "from(${c.camelConsumerAddress}).to($inGoesTo).routeId(in-${c.id})"
			if(inGoesTo && c.camelConsumerAddress) routes << from(c.camelConsumerAddress).to(inGoesTo).routeId("in-${c.id}")
			println "out comes from: $outComesFrom"
			println "from($outComesFrom).to(${c.camelProducerAddress}).routeId(out-${c.id})"
			if(outComesFrom && c.camelProducerAddress) routes << from(outComesFrom).to(c.camelProducerAddress).routeId("out-${c.id}")
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
