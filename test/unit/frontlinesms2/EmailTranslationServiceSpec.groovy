package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import org.apache.camel.Exchange

import org.apache.camel.impl.DefaultExchange
import org.apache.camel.component.mail.MailBinding
import org.apache.camel.impl.DefaultHeaderFilterStrategy
import org.apache.camel.component.mail.MailEndpoint

class EmailTranslationServiceSpec extends UnitSpec {
	@Shared
	def t

	def setupSpec() {
		t = new EmailTranslationService()
	}

	def "it's a TransformProcessor"() {
		expect:
			t instanceof org.apache.camel.Processor
	}

	def "converted message is an Fmessage"() {
		given:
			def testExchange = createTestExchange()
		when:
			t.process(testExchange)
		then:
			assert testExchange.out.body instanceof Fmessage
	}

	def "email from field is converted to a suitable Fmessage from field"() {
		given:
			def testExchange = createTestExchange()
		when:
			t.process(testExchange)
		then:
			assert testExchange.out.body.src == "email:test@example.com"
	}

	def "email to field is converted to a suitable Fmessage to desitination field"() {
		given:
			def testExchange = createTestExchange()
		when:
			t.process(testExchange)
		then:
			assert testExchange.out.body.dst == "email:frontlinesms1@example.com"
	}

	def "email body is converted to a suitable Fmessage body"() {
		given:
			def testExchange = createTestExchange()
		when:
			t.process(testExchange)
		then:
			assert testExchange.out.body.text == """Hello

Here's the test email body converted into a textual message."""
	}

	private Exchange createTestExchange(def email = null) {
		email = email ?: createTestEmail()
		def camelMessage = new org.apache.camel.component.mail.MailMessage(email)
		new MailEndpoint('asdf').createExchange(email)
	}

	private javax.mail.Message createTestEmail() {
		def e = Mock(javax.mail.Message)

		def headers = [From: 'test@example.com', To: 'frontlinesms1@example.com', Subject: 'Hello']
		headers.each { k,v -> e.getHeader(k) >> v }
		e.getAllHeaders() >> asEnumeration(headers.collect { k,v -> new javax.mail.Header(k, v) })

		e.getContent() >> "Here's the test email body converted into a textual message."

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

