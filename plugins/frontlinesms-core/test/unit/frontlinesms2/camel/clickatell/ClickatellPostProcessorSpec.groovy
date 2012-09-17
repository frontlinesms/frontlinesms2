package frontlinesms2.camel.clickatell

import spock.lang.*
import org.apache.camel.Exchange
import org.apache.camel.Message
import frontlinesms2.camel.exception.*

class ClickatellPostProcessorSpec extends Specification {
	ClickatellPostProcessor p = new ClickatellPostProcessor()
	
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
			responseText << ["ERR: 1, something awful",
					"Server 500 error<br/>etc. etc."]
	}

	@Unroll
	def 'Relevant Exception should be thrown depending on the response from Clickatell Servers'() {
		when:
			def x = mockExchange(responseText)
			p.process(x)
		then:
			thrown(ex)
		where:
			responseText 				|ex
			"ERR: 001, Authentication Failure"	|AuthenticationException
			"ERR: 108, Invalid AppID"		|InvalidApiIdException
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
