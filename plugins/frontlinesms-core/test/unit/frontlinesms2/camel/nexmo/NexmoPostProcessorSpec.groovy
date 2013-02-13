package frontlinesms2.camel.nexmo

import spock.lang.*
import org.apache.camel.Exchange
import org.apache.camel.Message
import frontlinesms2.camel.exception.*

class NexmoPostProcessorSpec extends Specification {
	NexmoPostProcessor p = new NexmoPostProcessor()
	
	def 'Successful responses should do nothing exciting'() {
		given:
			def x = mockExchange(responseText)
		when:
			p.process(x)
		then:
			notThrown(RuntimeException)
		where:
			responseText << ["""{"message-count":"1","messages":[{"status":"0", "message-id":"00000123","to":"44123456789","remaining-balance":"1.10", "message-price":"0.05","network":"23410"}]}"""]
	}
	
	def 'Error responses should trigger RuntimeException'() {
		given:
			def x = mockExchange(responseText)
		when:
			p.process(x)
		then:
			thrown(RuntimeException)
		where:
			responseText << ["""{"message-count":"1","messages":[{"error-text":"Bad Credentials","status":"4"}]}"""]
	}

	@Unroll
	def 'Relevant Exception should be thrown depending on the response from Nexmo Servers'() {
		when:
			def x = mockExchange(responseText)
			p.process(x)
		then:
			thrown(ex)
		where:
			responseText 				        									    				    |    ex
			"""{"message-count":"1","messages":[{"status":"2","error-text":"Missing from param"}]}"""	    |    AuthenticationException
			"""{"message-count":"1","messages":[{"status":"9","error-text":"Pesa hazitoshi"}]}"""	        |    InsufficientCreditException
			"""{"message-count":"1","messages":[{"status":"16","error-text":"shida za muda kukimbia"}]}"""	|    RuntimeException
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
