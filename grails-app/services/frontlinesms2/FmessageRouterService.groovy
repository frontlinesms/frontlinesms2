package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Header

/** This is a Dynamic Router */
class FmessageRouterService {
	def camelContext

	/**
	 * Slip should return the list of ______ to forward to, or <code>null</code> if
	 * we've done with it.
	 */
	def slip(Exchange exchange, @Header(Exchange.SLIP_ENDPOINT) String previous, @Header('fconnection') String target) {	
		println "Routing exchange $exchange with previous endpoint $previous and target fconnection $target"
		if(previous) {
			// We only want to pass this message to a single endpoint, so if there
			// is a previous one set, we should exit the slip.
			println "Exchange has previous endpoint from this slip.  Returning null."
			return null
		} else if(target) {
			println "Target is set, so forwarding to specific fconnection"
			return "seda:out-$target"
		} else {
			println "We don't know what we're doing, so don't route anywhere"
			return null
		}
	}
}
