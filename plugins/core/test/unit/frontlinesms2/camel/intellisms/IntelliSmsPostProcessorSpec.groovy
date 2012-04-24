package frontlinesms2.camel.intellisms

import spock.lang.*
import grails.plugin.spock.*
import org.apache.camel.Exchange
import org.apache.camel.Message
import frontlinesms2.camel.exception.FatalConnectionException

class IntelliSmsPostProcessorSpec extends UnitSpec {
	IntelliSmsPostProcessor p = new IntelliSmsPostProcessor()
	
	def 'Successful responses should do nothing exciting'() {
		given:
			def x = mockExchange("ID: 2345678")
		when:
			p.process(x)
		then:
			notThrown(FatalConnectionException)
	}
	
	@Unroll
	def 'Error responses should trigger FatalConnectionException'() {
		given:
			def x = mockExchange(responseText)
		when:
			p.process(x)
		then:
			thrown(FatalConnectionException)
		where:
			responseText << ["ERR:LOGIN_INVALID"]
	}
	
	private def mockExchange(httpResponseText) {
		def x = Mock(Exchange)
		def inMessage = Mock(Message)
		inMessage.body >> httpResponseText
		inMessage.getBody(byte[]) >> httpResponseText.getBytes('UTF-8')
		x.in >> inMessage
		return x
	}
}
