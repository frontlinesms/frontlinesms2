package frontlinesms2

import org.apache.camel.component.mail.MailMessage

import javax.mail.Message;

import org.gmock.WithGMock

@WithGMock
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
					new Fmessage(src: 'alice', dst: 'bob', content: 'subject'))
			def message = mock(Message)
			message.getFrom().returns(emailAddress('alice@example.com'))
			message.getTo().returns(emailAddress('bob@example.de'))
			message.getSubject().returns('email subject')
		when:
			// FIXME body here should be a message as provided by camel email component
			template.sendBodyAndHeaders(new MailMessage(message), [:])
		then:
			resultEndpoint.assertIsSatisfied()
			assert Fmessage.count() == 1
	}
}

