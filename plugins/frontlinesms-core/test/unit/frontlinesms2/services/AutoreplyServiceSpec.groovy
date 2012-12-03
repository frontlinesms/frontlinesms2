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
			incoming.src >> "123"
			def outgoing = Mock(Fmessage)
			service.messageSendService = messageSendService
			invoker.getPropertyValue('autoreplyText') >> 'step autoreply text'
			invoker.autoreplyText >> 'activity autoreply text'
		when:
			service.doReply(invoker, incoming)
		then:
			1 * messageSendService.createOutgoingMessage(_) >> { Map req ->
				assert req.addresses == "123"
				return outgoing
			}
			1 * messageSendService.send(outgoing)
		where:
			invokerType << [Autoreply, ReplyActionStep]
	}
}