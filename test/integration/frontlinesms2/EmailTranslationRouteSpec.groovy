package frontlinesms2

import routing.CamelIntegrationSpec
import org.apache.camel.component.mail.MailMessage

class EmailTranslationRouteSpec extends CamelIntegrationSpec {
	String getFrom() {
		'seda:raw-email'
	}
	String getTo() {
		'seda:fmessages-to-store'
	}

	def "test translation route"() {
		given:
			resultEndpoint.expectedBodiesReceived(
					new Fmessage(src: 'alice', dst: 'bob', content: 'subject'))
			def mailMessage = mock(MailMessage) // FIXME add methods to mock return of required message properties
		when:
			template.sendBodyAndHeaders(mailMessage)
		then:
       			resultEndpoint.assertIsSatisfied()
			assert Fmessage.count() == 0		
	}
}

