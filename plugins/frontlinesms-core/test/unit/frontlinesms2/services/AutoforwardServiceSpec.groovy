package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.test.mixin.*
import grails.buildtestdata.mixin.Build

@Mock([Autoforward, CustomActivity, TextMessage, ForwardActionStep, MessageDetail])
@TestFor(AutoforwardService)
@Build([Autoforward, CustomActivity, TextMessage, ForwardActionStep])
class AutoforwardServiceSpec extends Specification {
	def outgoingMessage
	def sendService

	def setup() {
		CustomActivity.metaClass.addToSteps = { s -> if(!steps) steps = []; steps << s }
		CustomActivity.metaClass.addToMessages = { m -> if(!messages) messages = []; messages << m; m.messageOwner = delegate }
		Autoforward.metaClass.addToMessages = { m -> if(!messages) messages = []; messages << m; m.messageOwner = delegate }
		outgoingMessage = TextMessage.build()

		sendService = Mock(MessageSendService)
		sendService.createOutgoingMessage(_) >> outgoingMessage
		service.messageSendService = sendService
	}

	def 'doForward() for a Step should send a message'() {
		given:
			def forwardStep = ForwardActionStep.build()
			forwardStep.grailsApplication = [domainClasses:[]]
			forwardStep.addToStepProperties(key:'recipient', value:'Address-12345')
			forwardStep.addToStepProperties(key:'sentMessageText', value:'sent this message')
			def owner = CustomActivity.build()

			def message = TextMessage.build()
			owner.addToMessages(message)
		when:
			service.doForward(forwardStep, message)
		then:
			1 * sendService.send(outgoingMessage)
	}

	def 'doForward() for a standard Activity should send a message'() {
		given:
			def a = Autoforward.build()
			def m = TextMessage.build(messageOwner:a)
		when:
			service.doForward(a, m)
		then:
			1 * sendService.send(outgoingMessage)
	}
}

