package frontlinesms2.services

import spock.lang.*
import grails.test.mixin.*

import org.apache.camel.Exchange
import org.apache.camel.Message
import frontlinesms2.*

@TestFor(SmppTranslationService)
class SmppTranslationServiceSpec extends Specification {
	def setup() {
		String.metaClass.truncate = { max=16 ->
			delegate.size() <= max? delegate: delegate.substring(0, max-1) + '…'
		}
	}
	def "incoming smpp message is translated into Fmessage"() {
		given:
			def messageText = "Test message"
			def source = "+254725672318"
			def testDate = "Fri, 20 Apr 2012 14:27:14 +0100"
			def date = new Date().parse("EEE, dd MMM yyyy hh:mm:ss Z", testDate).format("YYMMddHHmm")
			def testExchange = createTestExchange([source: source, body:messageText, date:date])
			println "Exchange HEADERS ${testExchange.in.headers}"
			println "Exchange BODY ${testExchange.in.body}"
		when:
			service.process(testExchange)
		then:
			1 * testExchange.in.setBody({ m ->
					(m.src == source) && (m.text == messageText) && (m.date == Date.parse("yyMMddHHmm", date))
				})
	}
	
	private Exchange createTestExchange(params = []) {
		def x = Mock(Exchange)
		def m = Mock(Message)
		m.headers >> ['CamelSmppSourceAddr':params.source, 'CamelSmppDoneDate':params.date]
		m.body >> params.body
		x.in >> m
		def m2 = Mock(Message)
		x.out >> m2
		x
	}
}

