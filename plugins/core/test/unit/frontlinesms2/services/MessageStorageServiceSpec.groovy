package frontlinesms2.services

import spock.lang.*
import grails.plugin.spock.*
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.Exchange
import org.apache.camel.CamelContext
import frontlinesms2.MessageStorageService
import frontlinesms2.Fmessage

class MessageStorageServiceSpec extends UnitSpec {
	@Shared
	MessageStorageService s

	def setupSpec() {
		s = new MessageStorageService()
	}

	def "it's a processor"() {
		expect:
			s instanceof org.apache.camel.Processor
	}

	def "it saves the incoming Fmessage"() {
		given:
			mockDomain(Fmessage.class)
			def m = new Fmessage(src:"12345", inbound:true, date:new Date())
		when:
			s.process(createTestExchange(m))
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

