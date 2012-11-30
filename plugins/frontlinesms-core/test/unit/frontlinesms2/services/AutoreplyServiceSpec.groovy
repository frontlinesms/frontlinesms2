package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.test.mixin.*

@TestFor(AutoreplyService)
class AutoreplyServiceSpec extends Specification {

	@Unroll
	def "doReply will create an outgoing message"() {
		setup:
			def invoker = Mock(invokerType)
			def messageSendService = Mock(MessageSendService)
			def incoming = Mock(Fmessage)
			def outgoing = Mock(Fmessage)
			service.messageSendService = messageSendService
		when:
			service.doReply(invoker, incoming)
		then:
			1 * messageSendService.createOutgoingMessage(_) >> { Map req -> assert true; return outgoing }
			1 * messageSendService.send(outgoing)
		where:
			invokerType << [Autoreply, ReplyActionStep]
	}
}