package routing

import frontlinesms2.*

class RoutingSpecUtils {
	static Fmessage createOutgoing(String dst, String text) {
		Fmessage m = new Fmessage(text:text, inbound:false, hasPending:true)
		m.addToDispatches(new Dispatch(dst:dst, status:DispatchStatus.PENDING))
		return m
	}

	static def waitFor(Closure c, pause=200, totalWait=5000) {
		for(long end = System.currentTimeMillis() + totalWait; System.currentTimeMillis() < end;) {
			if(c.call()) return true
			else sleep(pause)
		}
		return false
	}
}

