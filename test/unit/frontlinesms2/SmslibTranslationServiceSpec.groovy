package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import org.apache.camel.Exchange
import org.smslib.CStatusReportMessage

class SmslibTranslationServiceSpec extends UnitSpec {
	def t
	
	def setup() {
		t = new SmslibTranslationService()
	}
	
	def "check delivery reports and other status updates are ignored"() {
		given:
			def statusReportExchange = Mock(Exchange)
			def camelMessage = Mock(org.apache.camel.Message)
			statusReportExchange.getIn() >> camelMessage
			camelMessage.getBody() >> Mock(CStatusReportMessage)
			statusReportExchange.getOut() >> Mock(org.apache.camel.Message)
		when:
			t.toFmessage(statusReportExchange)
		then:
			0 * statusReportExchange.out
	}
}

