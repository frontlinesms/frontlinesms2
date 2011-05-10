package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class MessageControllerSpec extends ControllerSpec {
	def 'inbox closure requests correct messages'() {
		setup:
			mockDomain(Fmessage)
		when:
			controller.inbox()
		then:
			mockParams.inbound
	}

	def "sent closure requests correct messages"() {
		setup:
			mockDomain(Fmessage)
		when:
			controller.sent()
		then:
			!mockParams.inbound
	}
}

