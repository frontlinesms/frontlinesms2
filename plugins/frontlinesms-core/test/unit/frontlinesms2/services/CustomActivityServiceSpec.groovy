package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.test.mixin.*
import grails.buildtestdata.mixin.Build

@TestFor(CustomActivityService)
@Build(CustomActivity)
class CustomActivityServiceSpec extends Specification {
	def m, c, joinStep, leaveStep, replyStep, service
	def setup() {
		service = new CustomActivityService()
		createTestCustomActivity()
	}
	
	def "triggerSteps invokes doAction on each step"() {
		when:
			service.triggerSteps(c, m)
		then:
			1 * joinStep.doAction(_) >> { m -> assert true; return true }
			1 * leaveStep.doAction(_) >> { m -> assert true; return true }
			1 * replyStep.doAction(_) >> { m -> assert true; return true }
	}

	private def createTestCustomActivity() {
		m = Mock(Fmessage)
		joinStep = Mock(JoinActionStep)
		leaveStep = Mock(LeaveActionStep)
		replyStep = Mock(ReplyActionStep)
		joinStep.doAction >> { m -> println "I was called with $m"}
		leaveStep.doAction >> { m -> println "I was called with $m"}
		replyStep.doAction >> { m -> println "I was called with $m"}
		c = CustomActivity.build()
		[joinStep, leaveStep, replyStep].each { c.addToSteps(it) }
		println "c is $c and its steps are ${c.steps}"
	}
}
