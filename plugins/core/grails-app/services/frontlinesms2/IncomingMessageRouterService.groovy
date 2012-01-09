package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Header

class IncomingMessageRouterService {

	def incomingMessageProcessorService
	int counter = 0
	
	def slip(@Header(Exchange.SLIP_ENDPOINT) String previous) {
		if(previous) {
			// We only want to pass this message to a single endpoint, so if there
			// is a previous one set, we should exit the slip.
			println "Exchange has previous endpoint from this slip.  Returning null."
			return null
		} else {
			println "route definition is: ${incomingMessageProcessorService.routes}"
			def routeNames = ""
			if(incomingMessageProcessorService.routes.size() > 1) {
				routeNames = incomingMessageProcessorService.routes*.endpoint.endpointUri.join(", ")
			} else {
				routeNames = incomingMessageProcessorService.routes.endpoint.endpointUri
			}
			
			return routeNames
		}
		return null
	}
}