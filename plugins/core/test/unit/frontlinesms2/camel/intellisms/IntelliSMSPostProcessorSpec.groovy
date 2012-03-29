package frontlinesms2.camel.intellisms

import spock.lang.*
import grails.plugin.spock.*
import org.apache.camel.Exchange
import org.apache.camel.Message

class IntelliSMSPostProcessorSpec extends UnitSpec {
	IntelliSMSPostProcessor p = new IntelliSMSPostProcessor()
	
	def 'Successful responses should do nothing exciting'() {
		given:
			def x = mockExchange("ID: 2345678")
		when:
			p.process(x)
		then:
			notThrown(RuntimeException)
	}
	
	@Unroll
	def 'Error responses should trigger RuntimeException'() {
		given:
			def x = mockExchange(responseText)
		when:
			p.process(x)
		then:
			thrown(RuntimeException)
		where:
			responseText << ["ERR:LOGIN_INVALID",
					"Username or Password is invalid<br/>etc. etc."]
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
