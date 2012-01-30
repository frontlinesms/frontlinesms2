package frontlinesms2.services

import frontlinesms2.DispatchRouterService;
import frontlinesms2.Fmessage;
import frontlinesms2.Dispatch;
import frontlinesms2.DispatchStatus
import grails.plugin.spock.UnitSpec
import org.apache.camel.CamelContext
import org.apache.camel.Exchange

class DispatchRouterServiceSpec extends UnitSpec {
	def "should update the dispatch when no route is found"() {
		setup:
			def exchange = Mock(Exchange)
			def camelContext = Mock(CamelContext)
			camelContext.getRoutes()>> []

			def camelMessage = Mock(org.apache.camel.Message)
			exchange.getIn() >> camelMessage
			camelMessage.getBody() >> mockDomain(Dispatch, [new Dispatch(dst: "dst", message: new Fmessage())])

			def service = new DispatchRouterService()
			service.camelContext = camelContext
		when:
			service.slip(exchange, null, null)
		then:
			RuntimeException ex = thrown()
	}
}

