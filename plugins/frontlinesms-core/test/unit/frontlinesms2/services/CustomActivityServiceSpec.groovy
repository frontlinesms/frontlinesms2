package frontlinesms2.services

import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*

@TestFor(CustomActivityService)
@Mock([CustomActivity, JoinActionStep, LeaveActionStep, ReplyActionStep])
class CustomActivityServiceSpec extends Specification {
	
	def "triggerSteps() invokes doAction() on each step"() {
		setup:
			def m = Mock(Fmessage)
			def joinStep = Mock(JoinActionStep)
			def leaveStep = Mock(LeaveActionStep)
			def replyStep = Mock(ReplyActionStep)
			def c = Mock(CustomActivity)
			c.steps >> [joinStep, leaveStep, replyStep]
			println "c is $c and its steps are ${c.steps}"
			def service = new CustomActivityService()
		when:
			service.triggerSteps(c, m)
		then:
			1 * joinStep.doAction(_)
			1 * leaveStep.doAction(_)
			1 * replyStep.doAction(_)
	}
}
