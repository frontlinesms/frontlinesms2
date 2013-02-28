package frontlinesms2

import spock.lang.*

import frontlinesms2.CoreAppInfoProviders as CAIP

import static ConnectionStatus.*

class CoreAppInfoProvidersSpec extends Specification {
	@Unroll
	def 'test statusIndicatorProvider traffic lights'() {
		setup:
			Fconnection.metaClass.static.list = {
				statuses.collect { [status:it] }
			}
		when:
			def color = CAIP.statusIndicatorProvider(null, null, null)
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
			'orange'      | [CONNECTING, NOT_CONNECTED, DISABLED, CONNECTED]
			'green'       | [NOT_CONNECTED, CONNECTED, NOT_CONNECTED]
			'green'       | [DISABLED, NOT_CONNECTED, CONNECTED, NOT_CONNECTED]
			'green'       | [CONNECTED]
			'green'       | [CONNECTED, CONNECTED]
			'green'       | [DISABLED, CONNECTED]
			'green'       | [DISABLED, CONNECTED, CONNECTED]
			'red'         | [FAILED]
			'red'         | [FAILED, DISABLED]
			'red'         | [FAILED, NOT_CONNECTED]
			'red'         | [FAILED, CONNECTED]
	}
}

