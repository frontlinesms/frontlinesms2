package frontlinesms2.services

import spock.lang.*
import grails.test.mixin.*

import org.apache.camel.Exchange
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultExchange

import frontlinesms2.*

@TestFor(MessageStorageService)
@Mock(Fmessage)
class MessageStorageServiceSpec extends Specification {
	def setup() {
		Fmessage.metaClass.static.withSession = { Closure c -> }
	}

	def "it's a processor"() {
		expect:
			service instanceof org.apache.camel.Processor
	}

	def "it saves the incoming Fmessage"() {
		given:
			def m = new Fmessage(src:"12345", inbound:true, date:new Date())
		when:
			serviceprocess(createTestExchange(m))
		then:
			Fmessage.findAll() == [m]
	}

	def createTestExchange(def fmessage) {
		CamelContext context = Mock(CamelContext)
		Exchange exchange = new DefaultExchange(context)
		org.apache.camel.Message message = exchange.in
		message.setBody(fmessage);
		return exchange;
	}
}

