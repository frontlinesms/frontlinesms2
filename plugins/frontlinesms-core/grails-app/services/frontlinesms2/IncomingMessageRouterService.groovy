package frontlinesms2

import org.apache.camel.Endpoint

class IncomingMessageRouterService {
	def camelContext
	
	def Endpoint[] route(String body) {
		camelContext.routes*.endpoint.findAll { it.endpointUri.endsWith('-fmessages-to-process') }
	}
}
