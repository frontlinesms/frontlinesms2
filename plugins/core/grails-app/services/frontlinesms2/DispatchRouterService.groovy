package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Header

/** This is a Dynamic Router */
class DispatchRouterService {
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
			println "Target is set, so forwarding each dispatch to specific fconnection"
			return "seda:out-$target"
		} else {
			println "Routes available: ${camelContext.routes*.id}"
			def filteredRouteList = filter(camelContext.routes, { it.id.startsWith('out-') })
			if(filteredRouteList.size > 0) {
				println "Routes available: ${filteredRouteList*.id}"
				println "Counter has counted up to $counter"
				def routeName = "seda:${filteredRouteList[++counter % filteredRouteList.size].id}"
				println "Routing to $routeName"
				return routeName
			} else {
				// TODO may want to queue for retry here, after incrementing retry-count header
				throw new RuntimeException("No outbound route available for dispatch.")
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

	def handleCompleted(Exchange x) {
		println "DispatchRouterService.handleCompleted() : Updating status to SENT for ${x.in.body.getClass()}"
		updateDispatch(x, DispatchStatus.SENT)
	}

  def handleFailed(Exchange x) {
		println "DispatchRouterService.handleFailed() : Updating status to FAILED for ${x.in.body.getClass()}"
		updateDispatch(x, DispatchStatus.FAILED)
	}
	
	private Dispatch updateDispatch(Exchange x, DispatchStatus s) {
		println "DispatchRouterService.updateDispatch() : updateDispatch to $s..."
		def id = x.in.getHeader('frontlinesms.dispatch.id')

		def d
		if(x.in.body instanceof Dispatch) {
			d = x.in.body
		} else {
			d = Dispatch.get(id)
			println "DispatchRouterService.updateDispatch() : Dispatch.get($id) => $d"
		}
		println "DispatchRouterService.updateDispatch() : Updating dispatch: $d"
		if(d) {
			assert d instanceof Dispatch
			d.status = s
			d.save()
		}
		println "DispatchRouterService.updateDispatch() : Dispatch update completed."
	}
}
