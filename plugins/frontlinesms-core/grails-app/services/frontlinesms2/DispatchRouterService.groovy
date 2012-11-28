package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Header

/** This is a Dynamic Router */
class DispatchRouterService {
	def appSettingsService
	def camelContext

	int counter = -1

	/**
	 * Slip should return the list of ______ to forward to, or <code>null</code> if
	 * we've done with it.
	 */
	def slip(Exchange exchange,
			@Header(Exchange.SLIP_ENDPOINT) String previous,
			@Header('requested-fconnection-id') String requestedFconnectionId) {
		def log = { println "DispatchRouterService.slip() : $it" }
		log "ENTRY"
		log "Routing exchange $exchange with previous endpoint $previous and target fconnection $requestedFconnectionId"
		log "x.in=$exchange?.in"
		log "x.in.headers=$exchange?.in?.headers"

		if(previous) {
			// We only want to pass this message to a single endpoint, so if there
			// is a previous one set, we should exit the slip.
			log "Exchange has previous endpoint from this slip.  Returning null."
			return null
		} else if(requestedFconnectionId) {
			log "Target is set, so forwarding exchange to fconnection $requestedFconnectionId"
			return "seda:out-$requestedFconnectionId"
		} else {
			def routeId
			if(appSettingsService.get('routing.uselastreceiver') == true){
				def d = Dispatch.get(exchange.in.getHeader('frontlinesms.dispatch.id'))
				println "dispatch to send # $d ### d.dst # $d?.dst"
				def latestReceivedMessage = Fmessage.findBySrcAndOrderByDateCreated(d.dst)
				if(latestReceivedMessage?.receivedOn) {
					log "## Sending message with receivedOn Connection ##"
					def allOutRoutes = camelContext.routes.findAll { it.id.startsWith('out-') }
					println "Id of prefered route ## $latestReceivedMessage.receivedOn"
					println "allOutRoutes ## $allOutRoutes"
					def routeToTake = allOutRoutes.find{ it.id == "out-${latestReceivedMessage.receivedOn}" }
					println "Chosen Route ## $routeToTake"
					routeId = routeToTake?routeToTake.id:null
				}
			}

			if(!routeId){ // if uselastreceiver did not set the routeId
				if(appSettingsService.get('routing.otherwise') == 'any') {
					log "## Sending to any available connection ##"
					routeId = getDispatchRouteId()
				}else{
					log "## Not sending message at all ##"
				}
			}

			if(routeId) {
				log "Sending with route: $routeId"
				def fconnectionId = (routeId =~ /.*-(\d+)$/)[0][1]
				def queueName = "seda:out-$fconnectionId"
				log "Routing to $queueName"
				return queueName
			} else {
				// TODO may want to queue for retry here, after incrementing retry-count header
				throw new RuntimeException("No outbound route available for dispatch.")
			}
		}
	}
	
	def getDispatchRouteId() {
		def allOutRoutes = camelContext.routes.findAll { it.id.startsWith('out-') }
		if(allOutRoutes.size > 0) {
			// check for internet routes and prioritise them over modems
			def filteredRouteList = allOutRoutes.findAll { it.id.contains('-internet-') }
			if(!filteredRouteList) filteredRouteList = allOutRoutes.findAll { it.id.contains('-modem-') }
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
