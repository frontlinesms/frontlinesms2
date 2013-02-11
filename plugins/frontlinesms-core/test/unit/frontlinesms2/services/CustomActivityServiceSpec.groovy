package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.test.mixin.*
import grails.buildtestdata.mixin.Build

@TestFor(CustomActivityService)
@Mock(CustomActivity)
@Build(CustomActivity)
class CustomActivityServiceSpec extends Specification {
	def joinStep, leaveStep, replyStep, service
	def setup() {
		service = new CustomActivityService()
	}
	
	def "triggerSteps invokes doAction on each step"() {
		given:
			def m = Mock(Fmessage)
			def c = createTestCustomActivity()
		when:
			service.triggerSteps(c, m)
		then:
			1 * joinStep.process(_)
			1 * leaveStep.process(_)
			1 * replyStep.process(_)
	}

	private def createTestCustomActivity() {
		def c = CustomActivity.build()
		joinStep = Mock(JoinActionStep)
		leaveStep = Mock(LeaveActionStep)
		replyStep = Mock(ReplyActionStep)
		[joinStep, leaveStep, replyStep].each { c.addToSteps(it) }
		println "c is $c and its steps are ${c.steps}"
		c
	}
}
