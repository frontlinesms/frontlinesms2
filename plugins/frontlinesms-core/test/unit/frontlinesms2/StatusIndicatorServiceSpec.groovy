package frontlinesms2

import spock.lang.*
import static ConnectionStatus.*

class StatusIndicatorServiceSpec extends Specification {
	@Unroll
	def 'getColour() returns the appropriate colour when given a list of connection statuses'() {
		setup:
			Fconnection.metaClass.static.list = {
				statuses.collect { [status:it] }
			}
		when:
			def color = service.getColor()
		then:
			color == expectedColor
		where:
			expectedColor | statuses
			'grey'        | []
			'grey'        | [DISABLED]
			'grey'        | [NOT_CONNECTED, NOT_CONNECTED]
			'grey'        | [DISABLED, NOT_CONNECTED, NOT_CONNECTED]
			'orange'      | [CONNECTING]
			'orange'      | [CONNECTING, NOT_CONNECTED]
			'orange'      | [CONNECTING, NOT_CONNECTED, DISABLED]
			'green'       | [CONNECTING, NOT_CONNECTED, DISABLED, CONNECTED]
			'green'       | [NOT_CONNECTED, CONNECTED, NOT_CONNECTED]
			'green'       | [DISABLED, NOT_CONNECTED, CONNECTED, NOT_CONNECTED]
			'green'       | [CONNECTED]
			'green'       | [CONNECTED, CONNECTED]
			'green'       | [DISABLED, CONNECTED]
			'green'       | [DISABLED, CONNECTED, CONNECTED]
			'red'         | [FAILED]
			'red'         | [FAILED, DISABLED]
			'red'         | [FAILED, NOT_CONNECTED]
	}
}

