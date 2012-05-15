package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(StatusController)
class StatusControllerSpec extends Specification {
	@Unroll
	def 'test traffic lights'() {
		setup:
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

