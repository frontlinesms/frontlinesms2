package frontlinesms2

import org.apache.camel.component.mail.MailMessage

import javax.mail.Message;

class EmailTranslationRouteSpec extends EmailRouteSpec {
	String getFrom() {
		'seda:raw-email'
	}
	String getTo() {
		'seda:fmessages-to-store'
	}

	def "test translation route"() {
		given:
//			assert Fmessage.count() == 0
			def message = Mock(Message)
			message.getFrom() >> emailAddress('alice@example.com')
			message.getTo() >> emailAddress('bob@example.de')
			message.getSubject() >> 'email subject'
			message.getBody() >> 'email body'
		when:
			template.sendBodyAndHeaders(new MailMessage(message), [:])
		then:
//			assert Fmessage.count() == 0
			def exchanges = resultEndpoint.getReceivedExchanges()
//			println "Exchanges: ${exchanges.class} : ${exchanges}"
//			println "Exchanges size: ${exchanges.size()}"
//			println exchanges
//			assert exchanges == []
//			assert exchanges instanceof java.util.concurrent.CopyOnWriteArrayList
			exchanges.each() { println "Exchange entry: ${it}" }
			assert exchanges.size() == 1
			def receivedBody = exchanges[0].in.body
			assert receivedBody instanceof Fmessage
			assert receivedBody.src == 'email:alice@example.com'
			assert receivedBody.dst == 'email:bob@example.de'
			assert receivedBody.content == '''email subject
=============

email body'''
	}
}

