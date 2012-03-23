package frontlinesms2

import org.apache.camel.Exchange
import serial.SerialClassFactory
import serial.CommPortIdentifier
import net.frontlinesms.messaging.*

class FconnectionService {
	def messageSource
	def camelContext
	def deviceDetectionService
	
	def createRoutes(Fconnection c) {
		if(c instanceof SmslibFconnection) {
			deviceDetectionService.stopFor(c.port)
			// work-around for CORE-736 - NoSuchPortException can be thrown
			// for RXTX when a port has not previously been listed with
			// getPortIdentifiers()
			if(SerialClassFactory.instance?.serialPackageName == SerialClassFactory.PACKAGE_RXTX) {
				CommPortIdentifier.getPortIdentifiers()
			}
		}
		println "creating route for fconnection $c"
		try {
			def routes = c.routeDefinitions
			camelContext.addRouteDefinitions(routes)
			createSystemNotification("${messageSource.getMessage('connection.route.successNotification',[c?.name ?:c?.id] as Object[], Locale.setDefault(new Locale("en","US")))}")
			LogEntry.log("Created routes: ${routes*.id}")
		} catch(Exception e) {
			e.printStackTrace()
			log.warn("Error creating routes to fconnection with id $c?.id", e)
			LogEntry.log("Error creating routes to fconnection with name ${c?.name ?:c?.id}")
			createSystemNotification("${messageSource.getMessage('connection.route.failNotification',[c?.name ?:c?.id] as Object[], Locale.setDefault(new Locale("en","US")))}")
		}
	}
	
	private def createSystemNotification(String text) {
		def notification = SystemNotification.findByText(text) ?: new SystemNotification(text:text)
		notification.read = false
		notification.save(failOnError:true, flush:true)
	}
	
	def destroyRoutes(Fconnection c) {
		destroyRoutes(c.id as long)
		createSystemNotification("${messageSource.getMessage('connection.route.destroyNotification',[c?.name ?:c?.id] as Object[], Locale.setDefault(new Locale("en","US")))}")
	}
	
	def destroyRoutes(long id) {
		println "fconnectionService.destroyRoutes : ENTRY"
		println "fconnectionService.destroyRoutes : id=$id"
		camelContext.routes.filter { it.id ==~ /.*-$id$/ }.each {
			try {
				println "fconnectionService.destroyRoutes : route-id=$it"
				println "fconnectionService.destroyRoutes : stopping route $it..."
				camelContext.stopRoute(it.id)
				println "fconnectionService.destroyRoutes : $it stopped.  removing..."
				camelContext.removeRoute(it.id)
				println "fconnectionService.destroyRoutes : $it removed."
			} catch(Exception ex) {
				println "fconnectionService.destroyRoutes : Exception thrown while destroying $it: $ex"
				ex.printStackTrace()
			}
		}
		println "fconnectionService.destroyRoutes : EXIT"
	}
	
	def getRouteStatus(Fconnection c) {
		return camelContext.routes.any { it.id ==~ /.*-$c.id$/ } ? RouteStatus.CONNECTED : RouteStatus.NOT_CONNECTED
	}
	
	// TODO rename 'handleNotConnectedException'
	def handleDisconnection(Exchange ex) {
		try {
			println "fconnectionService.handleDisconnection() : ENTRY"
			def caughtException = ex.getProperty(Exchange.EXCEPTION_CAUGHT)
			println "FconnectionService.handleDisconnection() : ex.fromRouteId: $ex.fromRouteId"
			println "FconnectionService.handleDisconnection() : EXCEPTION_CAUGHT: $caughtException"
			
			log.warn("Caught exception for route: $ex.fromRouteId", caughtException)
			def routeId = (ex.fromRouteId =~ /(?:(?:in)|(?:out))-(\d+)/)[0][1]
			println "FconnectionService.handleDisconnection() : Looking to stop route: $routeId"
			RouteDestroyJob.triggerNow([routeId:routeId as long])
		} catch(Exception e) {
			e.printStackTrace()
		}
	}
}
