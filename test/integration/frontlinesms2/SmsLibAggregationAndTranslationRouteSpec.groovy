package frontlinesms2

import routing.CamelIntegrationSpec
import net.frontlinesms.camel.smslib.IncomingSmslibCamelMessage

class SmsLibAggregationAndTranslationRouteSpec extends CamelIntegrationSpec {
	String getFrom() {
		''
	}
	String getTo() {
		''
	}

	def 'test route with simple message'() {
		given:
			resultEndpoint.expectedBodiesReceived(
					new Fmessage(src: 'tel:+123456789', dst: 'smslib:@simulated-device', content: "hey there, here's a simple message received over SMS"))
			mockCMessage = mock().getHeader('from').returns('+123456798')
					.getHeader('to').returns('simulated-device')
					.getText().returns("hey there, here's a simple message received over SMS")
		when:
			template.sendBodyAndHeaders(new IncomingSmslibCamelMessage(mockCMessage))
		then:
			resultEndpoint.assertIsSatisfied()
	}

	def 'test route with aggregation'() {
		given:
			resultEndpoint.expectedBodiesReceived(
					new Fmessage(src: '+123456789', dst: 'smslib:@simulated-device', content: "hey there, here's a contatenated message received over SMS"))
			mockCMessage1 = mock() // TODO
			mockCMessage2 = mock() // TODO
		when:
			template.sendBodyAndHeaders(new IncomingSmslibCamelMessage(mockCMessage1))
			template.sendBodyAndHeaders(new IncomingSmslibCamelMessage(mockCMessage2))
		then:
			resultEndpoint.assertIsSatisfied()
	}
}
