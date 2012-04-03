package frontlinesms2

import spock.lang.*

class StatusControllerSpec extends grails.plugin.spock.ControllerSpec {
	def setup() {
		registerMetaClass(Fconnection)
	}

	@Unroll
	def 'test traffic lights'() {
		setup:
			registerMetaClass Fconnection
			Fconnection.metaClass.static.list = {
				statuses.collect { [status:it] }
			}
		when:
			controller.trafficLightIndicator()
		then:
			controller.response.contentAsString == expectedColor
		where:
			expectedColor | statuses
			'red'         | []
			'red'         | [RouteStatus.NOT_CONNECTED, RouteStatus.NOT_CONNECTED]
			'green'       | [RouteStatus.NOT_CONNECTED, RouteStatus.CONNECTED, RouteStatus.NOT_CONNECTED]
			'green'       | [RouteStatus.CONNECTED, RouteStatus.CONNECTED]
	}
}

