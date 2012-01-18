package frontlinesms2.services

import frontlinesms2.DispatchRouterService;
import frontlinesms2.Fmessage;
import frontlinesms2.Dispatch;
import grails.plugin.spock.UnitSpec
import org.apache.camel.CamelContext
import org.apache.camel.Exchange

class DispatchRouterServiceSpec extends UnitSpec {
	def "should update the dispatch when no route is found"() {
		setup:
			mockDomain(Dispatch)
			def exchange = Mock(Exchange)
			def camelContext = Mock(CamelContext)
			camelContext.getRoutes()>> []

			def camelMessage = Mock(org.apache.camel.Message)
			exchange.getIn() >> camelMessage
			camelMessage.getBody() >> new Dispatch(dst: "dst", date: new Date(), message: new Fmessage())

			def service = new DispatchRouterService()
			service.camelContext = camelContext
		when:
			service.slip(exchange, null, null)
		then:
			Dispatch.findByDst("dst").status == DispatchStatus.FAILED
	}
}

