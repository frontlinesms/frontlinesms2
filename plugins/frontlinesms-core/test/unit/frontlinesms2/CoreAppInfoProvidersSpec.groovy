package frontlinesms2

import spock.lang.*

import frontlinesms2.CoreAppInfoProviders as CAIP

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
			'red'         | []
			'red'         | [ConnectionStatus.NOT_CONNECTED, ConnectionStatus.NOT_CONNECTED]
			'green'       | [ConnectionStatus.NOT_CONNECTED, ConnectionStatus.CONNECTED, ConnectionStatus.NOT_CONNECTED]
			'green'       | [ConnectionStatus.CONNECTED, ConnectionStatus.CONNECTED]
	}
}

