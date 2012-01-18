package frontlinesms2

class StatusControllerSpec extends grails.plugin.spock.ControllerSpec {
	def 'traffic light should show red when there are no routes'() {
		setup:
			mockRouteStati([])
		when:
			controller.trafficLightIndicator()
		then:
			controller.response.contentAsString == 'red'
	}
	
	def 'traffic light should show red when there are only NOT_CONNECTED routes'() {
		setup:
			mockRouteStati([RouteStatus.NOT_CONNECTED, RouteStatus.NOT_CONNECTED])
		when:
			controller.trafficLightIndicator()
		then:
			controller.response.contentAsString == 'red'
	}
	
	def 'traffic light should show red when some routes are NOT_CONNECTED and some are CONNECTED'() {
		setup:
			mockRouteStati([RouteStatus.NOT_CONNECTED, RouteStatus.CONNECTED, RouteStatus.NOT_CONNECTED])
		when:
			controller.trafficLightIndicator()
		then:
			controller.response.contentAsString == 'red'
	}
	
	def 'traffic light should show green when all routes are CONNECTED'() {
		setup:
			mockRouteStati([RouteStatus.CONNECTED, RouteStatus.CONNECTED])
		when:
			controller.trafficLightIndicator()
		then:
			controller.response.contentAsString == 'green'
	}
	
	private def mockRouteStati(stati) {
		Fconnection.metaClass.static.list = {
			stati.collect { [status:it] }
		}
	}
}