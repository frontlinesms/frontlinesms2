package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import org.smslib.NotConnectedException
import serial.SerialClassFactory
import serial.CommPortIdentifier
import net.frontlinesms.messaging.*

class FconnectionService {
	def camelContext
	def deviceDetectionService
	def camelRouteBuilder = new RouteBuilder() {
		@Override
		void configure() {}
		List getRouteDefinitions(Fconnection c) {
			def incoming
			def routes = []
			if(c instanceof SmslibFconnection) {
				incoming = 'seda:raw-smslib'
				routes << from("seda:out-${c.id}")
						.onException(NotConnectedException)
								.handled(true)
								.beanRef('fconnectionService', "handleDisconnection")
								.end()
						.beanRef('smslibTranslationService', 'toCmessage')
						.to(c.camelProducerAddress)
						.routeId("out-${c.id}")
			} else if(c instanceof EmailFconnection) {
				incoming = 'seda:raw-email'
				if(c.camelProducerAddress) {
					routes << from('seda:email-messages-to-send')
							.to(c.camelProducerAddress)
							.routeId("out-${c.id}")
				}
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
				routes << from(c.camelConsumerAddress)
						.onException(NotConnectedException)
								.handled(true)
								.beanRef('fconnectionService', "handleDisconnection")
								.end()
						.to(incoming).routeId("in-${c.id}")
			} else {
				println "not creating incoming route: from(${c.camelConsumerAddress}).to($incoming).routeId(in-${c.id})"
			}
			println "Routes created: $routes"
			getLog().info "Creating routes: $routes..."
			routes
		}
	}
	
	def createRoutes(Fconnection c) {
		if(c instanceof SmslibFconnection) {
			deviceDetectionService.stopFor(c.port)
			// work-around for CORE-736 - NoSuchPortException can be thrown
			// for RXTX when a port has not previously been listed with
			// getPortIdentifiers()
			if(SerialClassFactory.instance.serialPackageName == SerialClassFactory.PACKAGE_RXTX) {
				CommPortIdentifier.getPortIdentifiers()
			}
		}
		def routes = camelRouteBuilder.getRouteDefinitions(c)
		println "creating route for fconnection $c"
		try {
			camelContext.addRouteDefinitions(routes)
			createSystemNotification("Created route from ${c.camelConsumerAddress} and to ${c.camelProducerAddress}")
			LogEntry.log("Created route from ${c.camelConsumerAddress} and to ${c.camelProducerAddress}")
		} catch(Exception e) {
			e.printStackTrace()
			log.warn("Error creating routes to fconnection with id $c?.id", e)
			LogEntry.log("Error creating routes to fconnection with name ${c?.name ?:c?.id}")
			createSystemNotification(e.message)
		}
	}
	
	private def createSystemNotification(String text) {
		def notification = SystemNotification.findByText(text) ?: new SystemNotification(text:text)
		notification.read = false
		notification.save(failOnError:true, flush:true)
	}
	
	def destroyRoutes(Fconnection c) {
		destroyRoutes(c.id as long)
	}
	
	def destroyRoutes(long id) {
		println "fconnectionService.destroyRoutes : ENTRY"
		println "fconnectionService.destroyRoutes : id=$id"
		["in-$id", "out-$id"].each {
			try {
				println "fconnectionService.destroyRoutes : route-id=$it"
				println "fconnectionService.destroyRoutes : stopping route $it..."
				camelContext.stopRoute(it)
				println "fconnectionService.destroyRoutes : $it stopped.  removing..."
				camelContext.removeRoute(it)
				println "fconnectionService.destroyRoutes : $it removed."
			} catch(Exception ex) {
				println "fconnectionService.destroyRoutes : Exception thrown while destroying $it: $ex"
				ex.printStackTrace()
			}
		}
		println "fconnectionService.destroyRoutes : EXIT"
	}
	
	def getRouteStatus(Fconnection c) {
		(camelContext.getRoute("in-${c.id}") || camelContext.getRoute("out-${c.id}")) ? RouteStatus.CONNECTED : RouteStatus.NOT_CONNECTED 
	}
	
	// TODO rename 'handleNotConnectedException'
	def handleDisconnection(Exchange ex) {
		try {
			println "fconnectionService.handleDisconnection(ex) : ENTRY"
			def caughtException = ex.getProperty(Exchange.EXCEPTION_CAUGHT)
			println "Exchange: $ex"
			println "ex.fromRouteId: $ex.fromRouteId"
			println "ex.fromEndpoint: $ex.fromEndpoint"
			println "ex.exception: $ex.exception"
			println "EXCEPTION_CAUGHT: ${caughtException}"
			println "fconnectionService.handleDisconnection(ex) : EXIT"
			
			log.warn("Caught exception for route: $ex.fromRouteId", caughtException)
			def routeId = (ex.fromRouteId =~ /(?:(?:in)|(?:out))-(\d+)/)[0][1]
			println "Looking to stop route: $routeId"
			RouteDestroyJob.triggerNow([routeId:routeId as long])
		} catch(Exception e) {
			e.printStackTrace()
		}
	}
}
