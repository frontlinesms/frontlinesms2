package frontlinesms2.camel.nexmo

import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*

import grails.buildtestdata.mixin.Build

@Mock(NexmoFconnection)
@Build(NexmoFconnection)
class NexmoPreProcessorSpec extends CamelUnitSpecification {
	NexmoPreProcessor p
	
	def setup() {
		p = new NexmoPreProcessor()
	}
	
	def 'out_body should be set to message text'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			1 * x.out.setBody("simple")
	}
	
	def 'out_body should be URL-encoded'() {
		setup:
			buildTestConnection()
			def x = mockExchange("more complex")
		when:
			p.process(x)
		then:
			1 * x.out.setBody("more+complex")
	}
	
	def 'dispatch ID should be set in header'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.out.headers.'frontlinesms.dispatch.id' == '45678'
	}
	
	def 'message destination should be set and stripped of leading plus'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			x.out.headers.'nexmo.dst' == '1234567890'
	}
	
	def 'nexmo auth details should be set in header'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			p.process(x)
		then:
			println "x.out.headers :: ${x.out.headers}"
			x.out.headers.'nexmo.api_key' == 'ufunguo'
			x.out.headers.'nexmo.api_secret' == 'siri'
			x.out.headers.'nexmo.fromNumber' == '123321'
	}
	private NexmoFconnection buildTestConnection() {
		NexmoFconnection.build(api_key:'ufunguo', api_secret:'siri',fromNumber: "123321")
	}
}
