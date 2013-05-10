package frontlinesms2.camel.intellisms

import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*

import org.apache.camel.Exchange
import grails.buildtestdata.mixin.Build

@Mock([IntelliSmsFconnection, Dispatch])
@Build(IntelliSmsFconnection)
class IntelliSmsPreProcessorSpec extends CamelUnitSpecification {
	IntelliSmsPreProcessor p
	
	def setup() {
		IntelliSmsFconnection.build(username:'bob', password:'secret')
		p = new IntelliSmsPreProcessor()
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
}

