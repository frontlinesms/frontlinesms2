package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Header

/** This is a Dynamic Router */
class DispatchRouterService {
	def camelContext

	int counter = -1

	/**
	 * Slip should return the list of ______ to forward to, or <code>null</code> if
	 * we've done with it.
	 */
	def slip(Exchange exchange, @Header(Exchange.SLIP_ENDPOINT) String previous, @Header('fconnection') String target) {
		println "DispatchRouterService.slip() : Routing exchange $exchange with previous endpoint $previous and target fconnection $target"
		if(previous) {
			// We only want to pass this message to a single endpoint, so if there
			// is a previous one set, we should exit the slip.
			println "DispatchRouterService.slip() : Exchange has previous endpoint from this slip.  Returning null."
			return null
		} else if(target) {
			println "DispatchRouterService.slip() : Target is set, so forwarding exchange to fconnection $target"
			println "DispatchRouterService.slip() : Exchange properties: $exchange.properties"
			return "seda:out-$target"
		} else {
			println "DispatchRouterService.slip() : Routes available: ${camelContext.routes*.id}"
			def routeId = getDispatchRouteId()
			if(routeId) {
				println "DispatchRouterService.slip() : Sending with route: $routeId"
				def fconnectionId = (routeId =~ /.*-(\d+)$/)[0][1]
				println "DispatchRouterService.slip() : Setting header 'fconnection' to $fconnectionId"
				exchange.out.headers.fconnection = fconnectionId
				def routeName = "seda:out-$fconnectionId"
				println "DispatchRouterService.slip() : Routing to $routeName"
				return routeName
			} else {
				// TODO may want to queue for retry here, after incrementing retry-count header
				throw new RuntimeException("No outbound route available for dispatch.")
			}
		}
	}
	
	def getDispatchRouteId() {
		def allOutRoutes = camelContext.routes.filter { it.id.startsWith('out-') }
		if(allOutRoutes.size > 0) {
			// check for internet routes and prioritise them over modems
			def filteredRouteList = allOutRoutes.filter { it.id.contains('-internet-') }
			if(!filteredRouteList) filteredRouteList = allOutRoutes.filter { it.id.contains('-modem-') }
			if(!filteredRouteList) filteredRouteList = allOutRoutes
			
			println "DispatchRouterService.getDispatchConnectionId() : Routes available: ${filteredRouteList*.id}"
			println "DispatchRouterService.getDispatchConnectionId() : Counter has counted up to $counter"
			return filteredRouteList[++counter % filteredRouteList.size]?.id
		}
	}

	def handleCompleted(Exchange x) {
		println "DispatchRouterService.handleCompleted() : ENTRY"
		updateDispatch(x, DispatchStatus.SENT)
		println "DispatchRouterService.handleCompleted() : EXIT"
	}

	def handleFailed(Exchange x) {
		println "DispatchRouterService.handleFailed() : ENTRY"
		updateDispatch(x, DispatchStatus.FAILED)
		println "DispatchRouterService.handleFailed() : EXIT"
	}
	
	private Dispatch updateDispatch(Exchange x, s) {
		def id = x.in.getHeader('frontlinesms.dispatch.id')
		Dispatch d
		if(x.in.body instanceof Dispatch) {
			d = x.in.body
			d.refresh()
		} else {
			d = Dispatch.get(id)
		}
		
		println "DispatchRouterService.updateDispatch() : dispatch=$d" 
		
		if(d) {
			d.status = s
			if(s == DispatchStatus.SENT) d.dateSent = new Date()

			try {
				d.save(failOnError:true, flush:true)
			} catch(Exception ex) {
				log.error("Could not save dispatch $d with message $d.message", ex)
			}
		} else log.info("No dispatch found for id: $id")
	}
}
