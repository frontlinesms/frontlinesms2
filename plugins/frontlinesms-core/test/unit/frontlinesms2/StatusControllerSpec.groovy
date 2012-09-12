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
			'red'         | [ConnectionStatus.NOT_CONNECTED, ConnectionStatus.NOT_CONNECTED]
			'green'       | [ConnectionStatus.NOT_CONNECTED, ConnectionStatus.CONNECTED, ConnectionStatus.NOT_CONNECTED]
			'green'       | [ConnectionStatus.CONNECTED, ConnectionStatus.CONNECTED]
	}
}

