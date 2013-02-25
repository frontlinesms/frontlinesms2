package frontlinesms2.camel.smpp

import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*

import grails.buildtestdata.mixin.Build

@Mock(SmppFconnection)
@Build(SmppFconnection)
class SmppPreProcessorSpec extends CamelUnitSpecification {
	SmppPreProcessor p
	
	def setup() {
		p = new SmppPreProcessor()
	}
	
	def 'out_body should be set to message text'() {
		setup:
			buildTestConnection()
			def x = mockExchange("simple")
		when:
			println "Smpp Exchange : $x"
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
			x.in.headers.'smpp.dst' == '1234567890'
	}

	private SmppFconnection buildTestConnection() {
		SmppFconnection.build(name : "Test SmppFconnection", username : "test", password : "test", port : '5775', url : 'http://12.23.34.45', fromNumber : '+1223234', send : 'true', receive : 'true')
	}
}
