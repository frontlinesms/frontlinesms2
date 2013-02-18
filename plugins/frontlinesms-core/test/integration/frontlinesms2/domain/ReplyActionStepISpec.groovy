package frontlinesms2.domain

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class ReplyActionStepISpec extends IntegrationSpec{
	def "calling ReplyActionStep.process should set the proper ownerDetail for the outgoing message"() {
		when:
			def replyStep = new ReplyActionStep().addToStepProperties(new StepProperty(key:"autoreplyText", value:"i will send this"))

			def customActivity = new CustomActivity(name:'Do it all')
				.addToSteps(replyStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)
 
			def incomingMessage = Fmessage.build(text:"incoming message", messageOwner: customActivity)
			replyStep.process(incomingMessage)
		then:
			Fmessage.findByText("i will send this").ownerDetail == incomingMessage.id.toString()
	}	
}