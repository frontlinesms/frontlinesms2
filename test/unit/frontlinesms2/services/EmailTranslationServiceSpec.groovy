package frontlinesms2.services

import spock.lang.*
import grails.plugin.spock.*
import org.apache.camel.Exchange

import org.apache.camel.component.mail.MailEndpoint
import frontlinesms2.EmailTranslationService
import frontlinesms2.Fmessage

class EmailTranslationServiceSpec extends UnitSpec {
	@Shared
	def t

	def setupSpec() {
		t = new EmailTranslationService()
	}

	def "it's a Processor"() {
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
			def testExchange = createTestExchange([body:"Here's the test email body converted into a textual message."])
		when:
			t.process(testExchange)
		then:
			assert testExchange.out.body.text == "Here's the test email body converted into a textual message."
	}

	def "email subject is converted to a suitable Fmessage body"() {
		given:
			def testExchange = createTestExchange([subject:'Hello'])
		when:
			t.process(testExchange)
		then:
			assert testExchange.out.body.text == 'Hello'
	}

	def "email body and subject is converted to a suitable Fmessage body"() {
		given:
			def testExchange = createTestExchange([subject:'Hello', body:"Here's the test email body converted into a textual message."])
		when:
			t.process(testExchange)
		then:
			assert testExchange.out.body.text == """Hello
=====

Here's the test email body converted into a textual message."""
	}

	private Exchange createTestExchange(def params = []) {
		def email = createTestEmail(params)
		new MailEndpoint('asdf').createExchange(email)
	}

	private javax.mail.Message createTestEmail(def params) {
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

