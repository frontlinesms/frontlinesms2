package frontlinesms2.camel.clickatell

import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*

import grails.buildtestdata.mixin.Build

@Mock(ClickatellFconnection)
@Build(ClickatellFconnection)
class ClickatellPreProcessorSpec extends CamelUnitSpecification {
	ClickatellPreProcessor p
	
	def setup() {
		def c = ClickatellFconnection.build(apiId:'11111', username:'bob', password:'secret')
		p = new ClickatellPreProcessor()
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
			x.out.headers.'clickatell.dst' == '1234567890'
	}
	
	def 'clickatell auth details should be set in header'() {
		given:
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.out.headers.'clickatell.apiId' == '11111'
			x.out.headers.'clickatell.username' == 'bob'
			x.out.headers.'clickatell.password' == 'secret'
		
	}
}
