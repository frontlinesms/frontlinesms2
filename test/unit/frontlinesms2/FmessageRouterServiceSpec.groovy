package frontlinesms2

import frontlinesms2.enums.MessageStatus
import grails.plugin.spock.UnitSpec
import org.apache.camel.CamelContext
import org.apache.camel.Exchange

class FmessageRouterServiceSpec extends UnitSpec {
	def "should update the message when no route is found"() {
		setup:
			mockDomain(Fmessage)
			def exchange = Mock(Exchange)
			def camelContext = Mock(CamelContext)
			camelContext.getRoutes()>> []

			def camelMessage = Mock(org.apache.camel.Message)
			exchange.getIn() >> camelMessage
			camelMessage.getBody() >> new Fmessage(src: "src")

			def service = new FmessageRouterService()
			service.camelContext = camelContext
		when:
			service.slip(exchange, null, null)
		then:
			Fmessage.findBySrc("src").status == MessageStatus.SEND_FAILED
	}
}

