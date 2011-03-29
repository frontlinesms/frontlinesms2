package frontlinesms2

import routing.CamelIntegrationSpec

import net.frontlinesms.camel.smslib.IncomingSmslibCamelMessage

import org.smslib.CIncomingMessage

class SmsLibAggregationAndTranslationRouteSpec extends CamelIntegrationSpec {
	String getFrom() {
		'seda:raw-smslib'
	}
	String getTo() {
		'seda:fmessages-to-store'
	}

	def 'test route with simple message'() {
		given:
			resultEndpoint.expectedBodiesReceived(
					new Fmessage(src: 'tel:+123456789', dst: 'smslib:@simulated-device', content: "hey there, here's a simple message received over SMS"))
		when:
			template.sendBodyAndHeaders(new IncomingSmslibCamelMessage(
					mockMessage('+123456798', "hey there, here's a simple message received over SMS")
					), [:])
		then:
			resultEndpoint.assertIsSatisfied()
	}

	def 'test route with aggregation'() {
		given:
			resultEndpoint.expectedBodiesReceived(
					new Fmessage(src: '+123456789', dst: 'smslib:@simulated-device', content: "hey there, here's a contatenated message received over SMS"))
		when:
			template.sendBodyAndHeaders(new IncomingSmslibCamelMessage(
					mockMultipartMessage('+123456798', 51, 1, 2, "hey there, here's a contatenated")
					), [:])
			template.sendBodyAndHeaders(new IncomingSmslibCamelMessage(
					mockMultipartMessage('+123456798', 51, 2, 2, ' message received over SMS')
					), [:])
		then:
			resultEndpoint.assertIsSatisfied()
	}

	private CIncomingMessage mockMultipartMessage(String sender, int multipartReference, int partNumber, int partCount, String text) {
		def mockMessage = mockMessage(sender, text)
		mockMessage.getMpRefNo() >> multipartReference
		mockMessage.getMpSeqNo() >> partNumber
		mockMessage.getMpMaxNo() >> partCount
		mockMessage.isMultipart() >> true
		mockMessage
	}

	private CIncomingMessage mockMessage(String sender, String text) {
		def mockMessage = Mock(CIncomingMessage)
		mockMessage.getOriginator() >> sender
		mockMessage.getText() >> text
		mockMessage
	}
}
