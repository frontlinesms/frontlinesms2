package frontlinesms2

import routing.CamelIntegrationSpec

import javax.mail.Address;
import javax.mail.internet.InternetAddress
import org.apache.camel.Exchange
import org.apache.camel.component.mail.MailEndpoint
import org.apache.camel.component.mail.MailMessage;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 16/03/11
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
abstract class EmailRouteSpec extends CamelIntegrationSpec {
	protected Address[] emailAddress(String... strings) {
		def addresses = []
		for(s in strings) addresses << new InternetAddress(s)
		return addresses as Address[]
	}

	protected MailMessage createMailMessage(def params = []) {
		createTestExchange(params).in
	}

	protected Exchange createTestExchange(def params = []) {
		def email = createTestEmail(params)
		new MailEndpoint('asdf').createExchange(email)
	}

	protected javax.mail.Message createTestEmail(def params) {
		def e = Mock(javax.mail.Message)

		def headers = [From: 'test@example.com', To: 'frontlinesms1@example.com', Subject: params.subject?:'']
		headers.each { k,v -> e.getHeader(k) >> v }
		e.getAllHeaders() >> asEnumeration(headers.collect { k,v -> new javax.mail.Header(k, v) })

		e.getContent() >> params.body

		return e
	}

	def asEnumeration = { list ->
		def iterator = list.iterator()
		[
			hasMoreElements: { iterator.hasNext() },
			nextElement: { iterator.next() }
		] as Enumeration
	}
}
