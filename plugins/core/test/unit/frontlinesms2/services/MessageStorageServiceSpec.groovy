package frontlinesms2.services

import spock.lang.*
import grails.test.mixin.*

import org.apache.camel.Exchange
import org.apache.camel.CamelContext
import org.apache.camel.Message
import org.apache.camel.impl.DefaultExchange

import frontlinesms2.*

@TestFor(MessageStorageService)
@Mock(Fmessage)
class MessageStorageServiceSpec extends Specification {
	def "it's a processor"() {
		expect:
			service instanceof org.apache.camel.Processor
	}

	def "it saves the incoming Fmessage"() {
		given:
			def m = new Fmessage(src:"12345", inbound:true, date:new Date())
		when:
			service.process(createTestExchange(m))
		then:
			Fmessage.findAll() == [m]
	}

	def createTestExchange(def fmessage) {
		CamelContext context = Mock(CamelContext)
		def exchange = new DefaultExchange(context)
		def message = exchange.in
		message.setBody(fmessage)
		return exchange
	}
}

