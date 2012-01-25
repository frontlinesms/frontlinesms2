package frontlinesms2.services

import spock.lang.*
import grails.plugin.spock.*
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.Exchange
import org.apache.camel.CamelContext
import frontlinesms2.DispatchStorageService
import frontlinesms2.Fmessage
import frontlinesms2.Dispatch
import frontlinesms2.DispatchStatus

class DispatchStorageServiceSpec extends UnitSpec {
	@Shared
	DispatchStorageService s

	def setupSpec() {
		s = new DispatchStorageService()
	}

	def "it's a processor"() {
		expect:
			s instanceof org.apache.camel.Processor
	}

	def "it saves the incoming Fmessage"() {
		given:
			mockDomain(Fmessage.class)
			mockDomain(Dispatch.class)
			def m = new Fmessage(date: new Date(), inbound: true)
			def d = new Dispatch(dst:'123456', status: DispatchStatus.PENDING)
			m.addToDispatches(d)
			m.save(flush: true)
		when:
			s.process(createTestExchange(d))
		then:
			Fmessage.findAll() == [m]
	}

	def createTestExchange(def dispatch) {
		CamelContext context = Mock(CamelContext)
		Exchange exchange = new DefaultExchange(context)
		org.apache.camel.Message message = exchange.in
		message.setBody(dispatch);
		return exchange;
	}
}

