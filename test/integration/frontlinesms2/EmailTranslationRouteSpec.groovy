package frontlinesms2

import org.apache.camel.component.mail.MailMessage

import javax.mail.Message;

import org.gmock.WithGMock

@WithGMock
class EmailTranslationRouteSpec extends EmailRouteSpec {
	String getFrom() {
		'seda:raw-email'
	}
	String getTo() {
		'seda:fmessages-to-store'
	}

	def "test translation route"() {
		given:
			resultEndpoint.expectedBodiesReceived(
					new Fmessage(src: 'alice', dst: 'bob', content: '''email subject
=============

email body'''))
			def message = mock(Message)
			message.getFrom().returns(emailAddress('alice@example.com'))
			message.getTo().returns(emailAddress('bob@example.de'))
			message.getSubject().returns('email subject')
			message.getBody().returns('email body')
		when:
			template.sendBodyAndHeaders(new MailMessage(message), [:])
		then:
       		resultEndpoint.assertIsSatisfied()
			assert Fmessage.count() == 0		
	}
}

