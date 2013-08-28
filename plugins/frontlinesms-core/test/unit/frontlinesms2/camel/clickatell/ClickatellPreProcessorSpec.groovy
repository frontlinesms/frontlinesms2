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
		p = new ClickatellPreProcessor()
	}
	
	def 'out_body should be set to message text'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			1 * x.in.setBody("simple")
	}
	
	def 'out_body should be URL-encoded'() {
		setup:
			buildTestConnection()
			def x = mockExchange("more complex")
		when:
			p.process(x)
		then:
			1 * x.in.setBody("more+complex")
	}
	
	def 'dispatch ID should be set in header'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.in.headers.'frontlinesms.dispatch.id' == '45678'
	}
	
	def 'message destination should be set and stripped of leading plus'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.in.headers.'clickatell.dst' == '1234567890'
	}
	
	def 'clickatell auth details should be set in header'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.in.headers.'clickatell.apiId' == '11111'
			x.in.headers.'clickatell.username' == 'bob'
			x.in.headers.'clickatell.password' == 'secret'
			x.in.headers.'clickatell.fromNumber' == null
		
	}

	def 'clickatell fromNumber should be set in header if sendToUsa is true'() {
		setup:
			buildTestConnection(true)
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.in.headers.'clickatell.apiId' == '11111'
			x.in.headers.'clickatell.username' == 'bob'
			x.in.headers.'clickatell.password' == 'secret'
			x.in.headers.'clickatell.fromNumber' == '%2B123321'
		
	}

	private ClickatellFconnection buildTestConnection(sendToUsa=false) {
		if (sendToUsa)
			ClickatellFconnection.build(apiId:'11111', username:'bob', password:'secret', sendToUsa: true, fromNumber: "+123321")
		else
			ClickatellFconnection.build(apiId:'11111', username:'bob', password:'secret')
	}
}
