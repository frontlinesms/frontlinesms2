package frontlinesms2.services

import grails.plugin.spock.*
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.component.mail.MailEndpoint
import frontlinesms2.IntelliSmsTranslationService
import frontlinesms2.Fmessage
import java.util.Date

class IntelliSmsTranslationServiceSpec extends UnitSpec {

	def service

	def setup() {
		service = new IntelliSmsTranslationService()
	}
	
	def "incoming intellisms email message is translated into Fmessage"() {
		given:
			def testDate = "Fri, 20 Apr 2012 14:27:14 +0100"
			def date = new Date().parse("EEE, dd MMM yyyy hh:mm:ss Z", testDate)
			def testExchange = createTestExchange([source:'254725672318 <254725672318@messaging.intellisoftware.co.uk>', body:"Here's the test email body converted into a textual message.", date:testDate])
		when:
			service.process(testExchange)
		then:
			testExchange.out.body instanceof Fmessage
			testExchange.out.body.src == "+254725672318"
			testExchange.out.body.text == "Here's the test email body converted into a textual message."
			testExchange.out.body.date == date
	}
	
	def "translator ignores emails whose source is not intellisms"() {
		given:
			def testExchange = createTestExchange([source:'bob@gmail.com', body:"Where the party at?"])
		when:
			service.process(testExchange)
		then:
			!testExchange.out.body
	}
	
	private Exchange createTestExchange(def params = []) {
		def email = createTestEmail(params)
		new MailEndpoint('asdf').createExchange(email)
	}
	
	private javax.mail.Message createTestEmail(def params) {
		def e = Mock(javax.mail.Message)

		def headers = [From: params.source, To: 'test@frontlinesms2.com', Subject: params.subject?:'Message from 254725672318', Date: "${params.date}"]
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

