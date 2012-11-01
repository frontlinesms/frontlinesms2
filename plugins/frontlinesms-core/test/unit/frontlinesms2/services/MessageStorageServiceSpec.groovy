package frontlinesms2.services

import spock.lang.*
import grails.test.mixin.*

import org.apache.camel.Exchange
import org.apache.camel.CamelContext
import org.apache.camel.Message
import org.apache.camel.impl.DefaultExchange

import frontlinesms2.*

@TestFor(MessageStorageService)
@Mock([Fmessage, Fconnection])
class MessageStorageServiceSpec extends Specification {
	def "it's a processor"() {
		expect:
			service instanceof org.apache.camel.Processor
	}

	def "it saves the incoming Fmessage"() {
		given:
			def m = new Fmessage(text:'', src:"12345", inbound:true, date:new Date())
		when:
			service.process(createTestExchange(m))
		then:
			Fmessage.findAll() == [m]
	}

	def "incoming Fmessage is populated with receivedOn field using the connection id in the header"() {
		given:
			def m = new Fmessage(text:'', src:"12345", inbound:true, date:new Date())
			def conn = createTestConnection()
		when:
			service.process(createTestExchange(m, conn.getId())) // TODO: find a way to 
		then:
			Fmessage.findAll() == [m]
			m.receivedOn == conn
	}

	def createTestExchange(def fmessage, connectionId=null) {
		CamelContext context = Mock(CamelContext)
		def exchange = new DefaultExchange(context)
		def message = exchange.in
		message.setBody(fmessage)
		if (connectionId)
			message.setHeader("connection-id", connectionId)
		return exchange
	}

	def createTestConnection() {
		Fconnection f = Mock()
		f.getId() >> 123
		f
	}
}

