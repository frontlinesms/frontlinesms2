package frontlinesms2.domain

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class CustomActivityISpec extends IntegrationSpec {
	def "a custom activity can have one step"() {
		given:
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"football"))
			def customActivity = new CustomActivity(name:"Custom Activity")
			customActivity.addToSteps(joinStep)
			customActivity.save(flush:true, failOnError:true)
		expect:
			CustomActivity.count()
	}

	def "a custom activity can have many steps of different kinds"() {
		given:
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"football"))
			def replyStep = new ReplyActionStep().addToStepProperties(new StepProperty(key:"autoreplyText", value:"autoreply :)"))

			def customActivity = new CustomActivity(name:"Custom Activity")
			customActivity.addToSteps(joinStep)
			customActivity.addToSteps(replyStep)
			customActivity.save(flush:true)
		expect:
			CustomActivity.count()	
	}

	def "custom activity steps should be ordered according to the order of addition"() {
		given:
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"football"))
			def replyStep = new ReplyActionStep().addToStepProperties(new StepProperty(key:"autoreplyText", value:"autoreply :)"))
			def leaveStep = new LeaveActionStep().addToStepProperties(new StepProperty(key:"group", value:"Friends"))

			def customActivity = new CustomActivity(name:"Custom Activity")
			customActivity.addToSteps(joinStep)
			customActivity.addToSteps(replyStep)
			customActivity.addToSteps(leaveStep)
			customActivity.save(flush:true)
		expect:
			customActivity.steps.containsAll([joinStep, replyStep, leaveStep])
	}

	def "processKeyword should add the message to the CustomActivity and invoke CustomActivityService.triggerSteps"() {
		given:
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"football"))
			def customActivity = new CustomActivity(name:"Custom Activity")
			customActivity.addToSteps(joinStep)
			customActivity.save(flush:true, failOnError:true)
			def customActivityService = Mock(CustomActivityService)
			customActivity.customActivityService = customActivityService
			def m = new Fmessage(src:"123", text:"this is a message", inbound:true).save(failOnError:true)
		when:
			customActivity.processKeyword(m, Mock(Keyword))
		then:
			1 * customActivityService.triggerSteps(_, _) 
	}
}