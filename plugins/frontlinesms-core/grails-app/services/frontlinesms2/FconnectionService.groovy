package frontlinesms2

import org.apache.camel.*
import serial.SerialClassFactory
import serial.CommPortIdentifier
import net.frontlinesms.messaging.*

class FconnectionService {
	def camelContext
	def deviceDetectionService
	def i18nUtilService
	def smssyncService
	def logService
	def systemNotificationService
	def connectingIds = [].asSynchronized()
	def messageSource

	def createRoutes(Fconnection c) {
		println "FconnectionService.createRoutes() :: ENTRY :: $c"
		assert c.enabled
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
			connectingIds << c.id
			def routes = c.routeDefinitions
			camelContext.addRouteDefinitions(routes)
			systemNotificationService.create(code:'connection.route.successNotification', args:[c?.name?: c?.id], kwargs:[connection:c])
			logService.handleRouteCreated(c)
		} catch(FailedToCreateProducerException ex) {
			logFail(c, ex.cause)
		} catch(Exception ex) {
			logFail(c, ex)
			destroyRoutes(c.id as long)
		} finally {
			connectingIds -= c.id
		}
		println "FconnectionService.createRoutes() :: EXIT :: $c"
	}

	def destroyRoutes(Fconnection c) {
		destroyRoutes(c.id as long)
		systemNotificationService.create(code:'connection.route.disableNotification', args:[c?.name?: c?.id], kwargs:[connection:c])
	}

	def destroyRoutes(long id) {
		println "fconnectionService.destroyRoutes : ENTRY"
		println "fconnectionService.destroyRoutes : id=$id"
		camelContext.routes.findAll { it.id ==~ /.*-$id$/ }.each {
			try {
				println "fconnectionService.destroyRoutes : route-id=$it.id"
				println "fconnectionService.destroyRoutes : stopping route $it.id..."
				camelContext.stopRoute(it.id)
				println "fconnectionService.destroyRoutes : $it.id stopped.  removing..."
				camelContext.removeRoute(it.id)
				println "fconnectionService.destroyRoutes : $it.id removed."
			} catch(Exception ex) {
				println "fconnectionService.destroyRoutes : Exception thrown while destroying $it.id: $ex"
				ex.printStackTrace()
			}
		}
		// TODO fire event to announce that route was destroyed
		println "fconnectionService.destroyRoutes : EXIT"
	}

	def getConnectionStatus(Fconnection c) {
		if(!c.enabled) return ConnectionStatus.DISABLED
		if(c.id in connectingIds) {
			return ConnectionStatus.CONNECTING
		}
		if(camelContext.routes.any { it.id ==~ /.*-$c.id$/ }) {
			return ConnectionStatus.CONNECTED
		}
		if (c instanceof SmslibFconnection) {
			if(deviceDetectionService.isConnecting(((SmslibFconnection) c).port)) {
				return ConnectionStatus.CONNECTING
			}
			if(isFailed(c)) {
				return ConnectionStatus.FAILED
			}
			return ConnectionStatus.NOT_CONNECTED
		}
		return ConnectionStatus.FAILED
	}

	// TODO rename 'handleNotConnectedException'
	def handleDisconnection(Exchange ex) {
		try {
			println "fconnectionService.handleDisconnection() : ENTRY"
			def caughtException = ex.getProperty(Exchange.EXCEPTION_CAUGHT)
			println "FconnectionService.handleDisconnection() : ex.fromRouteId: $ex.fromRouteId"
			println "FconnectionService.handleDisconnection() : EXCEPTION_CAUGHT: $caughtException"

			log.warn("Caught exception for route: $ex.fromRouteId", caughtException)
			def routeId = (ex.fromRouteId =~ /(?:(?:in)|(?:out))-(?:[a-z]+-)?(\d+)/)[0][1]
			println "FconnectionService.handleDisconnection() : Looking to stop route: $routeId"
			systemNotificationService.create(code:'connection.route.exception', args:[routeId], kwargs:[exception:caughtException])
			RouteDestroyJob.triggerNow([routeId:routeId as long])
		} catch(Exception e) {
			e.printStackTrace()
		}
	}

	def enableFconnection(Fconnection c) {
		c.enabled = true
		if(!c.save(failOnError:true)) {
			generateErrorSystemNotifications(c)
		}
		createRoutes(c)
	}

	def disableFconnection(Fconnection c) {
		destroyRoutes(c)
		c.enabled = false
		c.save(failOnError:true)
	}

	private def generateErrorSystemNotifications(connectionInstance){
		def notificationText
		connectionInstance.errors.allErrors.collect { error ->
			notificationText = messageSource(error, null)
			systemNotificationService.create(code:'connection.error.onsave', args:[notificationText])
		}.join('\n')
	}

	private def logFail(c, ex) {
		ex.printStackTrace()
		log.warn("Error creating routes to fconnection with id $c?.id", ex)
		logService.handleRouteCreationFailed(c)
		systemNotificationService.create(code:'connection.route.failNotification', args:[c?.id, c?.name?:c?.id], kwargs:[exception: ex, connection: c])
	}

	private def isFailed(Fconnection c) {
		// TODO I rather suspect that this method needs implementing
		false
	}
}

