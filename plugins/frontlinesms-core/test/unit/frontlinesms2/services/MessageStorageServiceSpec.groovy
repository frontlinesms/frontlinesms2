package frontlinesms2.services

import spock.lang.*
import grails.test.mixin.*

import org.apache.camel.Exchange
import org.apache.camel.CamelContext
import org.apache.camel.Message
import org.apache.camel.impl.DefaultExchange

import frontlinesms2.*

@TestFor(MessageStorageService)
@Mock([TextMessage, MissedCall, Fconnection, SmslibFconnection])
class MessageStorageServiceSpec extends Specification {
	def conn 

	def setup() {
		// Not sure why this is necessary with Test Mixins, but it seems to be:
		Fconnection.metaClass.addToMessages = { m ->
			if(delegate.messages) delegate.messages << m
			else delegate.messages = [m]
			return delegate
		}

		conn = new SmslibFconnection(name:"testConnection", baud:"9600", port:"/dev/ttyUSB0", send:true).save(failOnError:true)
	}

	def "it's a processor"() {
		expect:
			service instanceof org.apache.camel.Processor
	}

	def "it saves the incoming TextMessage"() {
		given:
			def m = new TextMessage(text:'', src:"12345", inbound:true, date:new Date())
		when:
			service.process(createTestExchange(m, conn.id))
		then:
			TextMessage.findAll() == [m]
	}

	def "it can also save a MissedCall"() {
		given:
			def m = new MissedCall(src:"12345")
		when:
			service.process(createTestExchange(m, conn.id))
		then:
			MissedCall.findAll() == [m]
	}

	def createTestExchange(def fmessage, connectionId=null) {
		CamelContext context = Mock(CamelContext)
		def exchange = new DefaultExchange(context)
		def message = exchange.in
		message.setBody(fmessage)
		if (connectionId)
			message.setHeader(Fconnection.HEADER_FCONNECTION_ID, connectionId)
		return exchange
	}
}

