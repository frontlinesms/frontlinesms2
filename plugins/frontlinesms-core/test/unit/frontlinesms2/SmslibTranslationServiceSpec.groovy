package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

import org.apache.camel.Exchange
import org.apache.camel.Message
import org.smslib.CStatusReportMessage

@TestFor(SmslibTranslationService)
class SmslibTranslationServiceSpec extends Specification {
	def "check delivery reports and other status updates are ignored"() {
		given:
			def statusReportExchange = Mock(Exchange)
			def camelMessage = Mock(org.apache.camel.Message)
			statusReportExchange.getIn() >> camelMessage
			camelMessage.getBody() >> Mock(CStatusReportMessage)
			statusReportExchange.getOut() >> Mock(org.apache.camel.Message)
		when:
			service.toFmessage(statusReportExchange)
		then:
			0 * statusReportExchange.out
	}

	def 'toCmessage should not choke if message content is null'() {
		given:
			Fmessage m = Mock() // implicitly has dst and text set to null
			m.date >> new Date()
			Dispatch d = Mock()
			d.message >> m
			Message camelMessage = Mock()
			camelMessage.body >> d
			Exchange x = Mock()
			x.in >> camelMessage
			x.out >> Mock(Message)
		when:
			service.toCmessage(x)
		then:
			true
	}
}

