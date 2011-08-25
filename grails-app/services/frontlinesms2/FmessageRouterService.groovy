package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Header
import frontlinesms2.enums.MessageStatus

/** This is a Dynamic Router */
class FmessageRouterService {
	def camelContext

	int counter = 0

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
			println "Routes available: ${camelContext.routes*.id}"
			def filteredRouteList = filter(camelContext.routes, { it.id.startsWith('out-') })
			if(filteredRouteList.size > 0) {
				println "Routes available: ${filteredRouteList*.id}"
				println "We don't know what we're doing, so don't route anywhere"
				println "Counter has counted up to $counter"
				return "seda:${filteredRouteList[++counter % filteredRouteList.size].id}"
			} else {
				// TODO do something like increment retry header for message, and then re-add to queue
				println "Haven't found any routes. updating message status as failed"
				// TODO could we just return reference to message storage service here?
				def message = exchange.in.body
				message.status = MessageStatus.SEND_FAILED
				message.save()
				return null
			}
		}
	}



	def filter(List l, Closure c) {
		def r = []
		l.each {
			if(c(it)) r << it
		}
		r
	}
}
