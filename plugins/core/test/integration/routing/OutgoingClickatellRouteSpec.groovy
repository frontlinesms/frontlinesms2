package routing

import static routing.RoutingSpecUtils.*

import frontlinesms2.*

class OutgoingClickatellRouteSpec extends CamelIntegrationSpec {
	def messageSendService

	def 'send goes through to clickatell endpoint'() {
		given:
			// TODO use endpoint mocking as per https://camel.apache.org/mock.html#Mock-Mockingendpointsandskipsendingtooriginalendpoint
			// TODO set up and start clickatell route

			// message is initialised
			def m = createOutgoing('+447890123456', 'test message')
		when:
			messageSendService.send(m)
		then:
			// TODO check message was sent with correct body and TO fields
			true
	}
}

