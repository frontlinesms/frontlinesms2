package frontlinesms2.camel.smpp

import spock.lang.*
import org.apache.camel.Exchange
import org.apache.camel.Message
import frontlinesms2.camel.exception.*

class SmppPostProcessorSpec extends Specification {
	SmppPostProcessor p = new SmppPostProcessor()
	
	def 'Successful responses should do nothing exciting'() {
		given:
			def x = mockExchange("ID: 2345678")
		when:
			p.process(x)
		then:
			notThrown(RuntimeException)
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
