package frontlinesms2.camel.intellisms

import spock.lang.*
import frontlinesms2.*
import grails.plugin.spock.*
import org.apache.camel.Exchange
import org.apache.camel.Message

class IntelliSMSPreProcessorSpec extends UnitSpec {
	IntelliSMSPreProcessor p
	
	def setup() {
		mockDomain IntelliSMSFconnection, [[username:'bob', password:'secret'] as IntelliSMSFconnection]
		
		registerMetaClass Exchange
		Exchange.metaClass.getFconnectionId = { IntelliSMSFconnection.list()[-1].id }
		
		p = new IntelliSMSPreProcessor()
	}
	
	def 'out_body should be set to message text'() {
		given:
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			1 * x.out.setBody("simple")
	}
	
	def 'out_body should be URL-encoded'() {
		given:
			def x = mockExchange("more complex")
		when:
			p.process(x)
		then:
			1 * x.out.setBody("more+complex")
	}
	
	def 'dispatch ID should be set in header'() {
		given:
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.out.headers.'frontlinesms.dispatch.id' == '45678'
	}
	
	def 'message destination should be set and stripped of leading plus'() {
		given:
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.out.headers.'intellisms.dst' == '1234567890'
	}
	
	def 'intellisms auth details should be set in header'() {
		given:
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.out.headers.'intellisms.username' == 'bob'
			x.out.headers.'intellisms.password' == 'secret'
		
	}
	
	private def mockDispatchMessage(String messageText) {
		def m = Mock(Message)
		m.body >> [
			id:'45678',
			message:[
				text:messageText,
				toString:{"mock Fmessage"}
			],
			dst:'+1234567890',
			toString:{"mock body (Dispatch)"}
		]
		return m
	}
	
	private Exchange mockExchange(messageText) {
		def inMessage = mockDispatchMessage(messageText)
		def x = Mock(Exchange)
		x.in >> inMessage
		def out = Mock(Message)
		out.headers >> [:]
		x.out >> out
		println "mockExchange() : x.out=$x.out"
		return x
	}
}
