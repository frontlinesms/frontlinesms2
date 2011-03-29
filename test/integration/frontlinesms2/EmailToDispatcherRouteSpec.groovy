package frontlinesms2

import org.apache.camel.component.mail.MailMessage

import javax.mail.Message;

class EmailToDispatcherRouteSpec extends EmailRouteSpec {
	String getFrom() {
		'seda:raw-email'
	}
	String getTo() {
		'seda:fmessages-to-process'
	}

	def "complete route test"() {
		given:
			resultEndpoint.expectedBodiesReceived(
					new Fmessage(src: 'alice', dst: 'bob', content: '''email subject
#############

email body'''))
			def message = Mock(Message)
			message.getFrom() >> emailAddress('alice@example.com')
			message.getTo() >> emailAddress('bob@example.de')
			message.getSubject() >> 'email subject'
			message.getBody() >> 'email body'
		when:
			template.sendBodyAndHeaders(new MailMessage(message), [:])
		then:
			resultEndpoint.assertIsSatisfied()
			assert Fmessage.count() == 1
	}
}

