package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.test.mixin.*

@Mock([ForwardActionStep, StepProperty, Fmessage, MessageSendService])
@TestFor(AutoforwardService)
class AutoforwardServiceSpec extends Specification {
	def 'calling autoforwardService.doForward from a forward step should result in message sent'() {
		setup:
			def forwardStep = Mock(ForwardActionStep)
			def sendService = Mock(MessageSendService)
			def owner = Mock(CustomActivity)
			
			def message = Mock(Fmessage)
			message.id >> 1
			owner.addToMessages(_) >> { ms-> return "adding message to messageOwner"}
			message.messageOwner >> owner
			
			def outgoingMessage = Mock(Fmessage)
			outgoingMessage.setOwnerDetail(_,_) >> "setting the owner detail"
			
			sendService.createOutgoingMessage(_) >> outgoingMessage

			forwardStep.getSentMessageText >> "sent this message"
			forwardStep.getRecipients() >> ["12345"]
			service.messageSendService = sendService
		when:
			service.doForward(forwardStep, message)
		then:
			1 * sendService.send(outgoingMessage)
	}
}
