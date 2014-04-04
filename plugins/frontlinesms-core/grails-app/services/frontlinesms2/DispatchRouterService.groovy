package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Header

import frontlinesms2.camel.exception.NoRouteAvailableException

/** This is a Dynamic Router */
class DispatchRouterService {
	static final String RULE_PREFIX = "fconnection-"
	def appSettingsService
	def camelContext
	def systemNotificationService

	int counter = -1
	/**
	 * Slip should return the list of ______ to forward to, or <code>null</code> if
	 * we've done with it.
	 */
	def slip(Exchange exchange,
			@Header(Exchange.SLIP_ENDPOINT) String previous,
			@Header('requested-fconnection-id') String requestedFconnectionId) {
		def logWithPrefix = { log.info "slip() : $it" }
		logWithPrefix "ENTRY"
		logWithPrefix "Routing exchange $exchange with previous endpoint $previous and target fconnection $requestedFconnectionId"
		logWithPrefix "x.in=$exchange?.in"
		logWithPrefix "x.in.headers=$exchange?.in?.headers"

		if(previous) {
			// We only want to pass this message to a single endpoint, so if there
			// is a previous one set, we should exit the slip.
			logWithPrefix "Exchange has previous endpoint from this slip.  Returning null."
			return null
		} else if(requestedFconnectionId) {
			logWithPrefix "Target is set, so forwarding exchange to fconnection $requestedFconnectionId"
			return "seda:out-$requestedFconnectionId"
		} else {
			def routeId
			logWithPrefix "appSettingsService.['routing.use'] is ${appSettingsService.get('routing.use')}"

			if(appSettingsService.get('routing.use')) {
				def fconnectionRoutingList = appSettingsService.get('routing.use').split(/\s*,\s*/)
				fconnectionRoutingList = fconnectionRoutingList.collect { route ->
					route.startsWith(RULE_PREFIX)? route.substring(RULE_PREFIX.size()): route
				}
				logWithPrefix "fconnectionRoutingList::: $fconnectionRoutingList"
				for(route in fconnectionRoutingList) {
					if(route == 'uselastreceiver') {
						def fconnection = getLastReceiverConnection(exchange)
						route = fconnection?.id
						routeId = fconnection ? getCamelRouteId(fconnection) : null
					} else {
						routeId = getCamelRouteId(Fconnection.get(route))
					}
					logWithPrefix "Route Id selected: $routeId"
					if(routeId) {
						if(exchange.in.body instanceof Dispatch) {
							def dispatch = exchange.in.body
							logWithPrefix " dispatch.fconnectionId:::: $route"
							dispatch.fconnectionId = route as Long
							dispatch.save(failOnError:true, flush:true)
						}
						break
					}
				}
			}

			if(routeId) {
				logWithPrefix "Sending with route: $routeId"
				def fconnectionId = (routeId =~ /.*-(\d+)$/)[0][1]
				def queueName = "seda:out-$fconnectionId"
				logWithPrefix "Routing to $queueName"
				return queueName
			} else { logWithPrefix "## Not sending message at all ##" }

			throw new NoRouteAvailableException()
		}
	}
	
	def getRouteIdByRoundRobin() {
		def allOutRoutes = camelContext.routes.findAll { it.id.startsWith('out-') }
		if(allOutRoutes.size > 0) {
			// check for internet routes and prioritise them over modems
			def filteredRouteList = allOutRoutes.findAll { it.id.contains('-internet-') }
			if(!filteredRouteList) filteredRouteList = allOutRoutes.findAll { it.id.contains('-modem-') }
			if(!filteredRouteList) filteredRouteList = allOutRoutes
			
			log.info "getRouteIdByRoundRobin() : Routes available: ${filteredRouteList*.id}"
			log.info "getRouteIdByRoundRobin() : Counter has counted up to $counter"
			return filteredRouteList[++counter % filteredRouteList.size]?.id
		}
	}

	def handleCompleted(Exchange x) {
		log.info "handleCompleted() : ENTRY"
		def connection = Fconnection.get(x.in.getHeader('fconnection-id'))
		connection?.updateDispatch(x)
		log.info "handleCompleted() : EXIT"
	}

	def handleFailed(Exchange x) {
		log.info "handleFailed() : ENTRY"
		log.info "handleFailed() : exchange $x"
		log.info "handleFailed() : exchange.in.headers ${x.in.headers}"
		log.info "handleFailed() : exchange.in.body ${x.in.body}"
		updateDispatch(x, DispatchStatus.FAILED)
		log.info "handleFailed() : EXIT"
	}

	def handleNoRoutes(Exchange x) {
		log.info "handleNoRoutes() : NoRouteAvailableException handling..."
		systemNotificationService.create(code:"routing.notification.no-available-route")
		x.out.body = x.in.body
		x.out.headers = x.in.headers
		log.info "handleNoRoutes() : EXIT"
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
		
		log.info "updateDispatch() : dispatch=$d" 
		
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

	private getLastReceiverConnection(exchange) {
		def logWithPrefix = { log.info "slip() : $it" }
		logWithPrefix "Dispatch is ${exchange.in.getBody()}"
		def d = exchange.in.getBody()
		logWithPrefix "dispatch to send # $d ### d.dst # $d?.dst"
		def latestReceivedMessage = TextMessage.findBySrc(d.dst, [sort: 'dateCreated', order:'desc'])
		logWithPrefix "## latestReceivedMessage ## is $latestReceivedMessage"
		latestReceivedMessage?.receivedOn
	}

	private getCamelRouteId(connection) {
		if(!connection) return null
		log.info "## Sending message with Connection with $connection ##"
		def allOutRoutes = camelContext.routes.findAll { it.id.startsWith('out-') }
		def routeToTake = allOutRoutes.find{ it.id.endsWith("-${connection.id}") }
		log.info "Chosen Route ## $routeToTake"
		routeToTake? routeToTake.id: null
	}
}

