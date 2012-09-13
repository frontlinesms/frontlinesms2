package routing

import static routing.RoutingSpecUtils.*

import frontlinesms2.*

class OutgoingEmailRouteSpec extends CamelIntegrationSpec {
	def messageSendService

	def 'test send message route'() {
		given:
			// TODO use mockmail framework as described at https://camel.apache.org/tutorial-example-reportincident-part3.html
			// TODO set up and start email route

			// message is initialised
			def m = createOutgoing('+447890123456', 'test message')
		when:
			messageSendService.send(m)
		then:
			// TODO check message was sent with correct body and TO fields
			true
	}
}

