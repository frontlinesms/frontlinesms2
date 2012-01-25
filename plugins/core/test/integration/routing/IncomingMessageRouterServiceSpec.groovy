package routing

import frontlinesms2.*

class IncomingMessageRouterServiceSpec extends CamelIntegrationSpec {
	def incomingMessageRouterService
	
	String getTestRouteFrom() { '' }
	String getTestRouteTo() { '' }
	
	def "should aflag new incoming messages as inbound"() {
		when:
			def routes = incomingMessageRouterService.slip(null)
		then:
			println "routes: $routes"
			routes == ["seda://incoming-fmessages-to-process"]
	}
}