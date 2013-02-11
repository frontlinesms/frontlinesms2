package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.test.mixin.*
import grails.buildtestdata.mixin.Build

@TestFor(CustomActivityService)
@Mock([CustomActivity])
class CustomActivityServiceSpec extends Specification {
	
	def "triggerSteps invokes doAction on each step"() {
		given:
			def m = Mock(Fmessage)
			def joinStep = Mock(JoinActionStep)
			def leaveStep = Mock(LeaveActionStep)
			def replyStep = Mock(ReplyActionStep)
			def c = new CustomActivity(name:"test", steps:[joinStep, leaveStep, replyStep])
		when:
			service.triggerSteps(c, m)
		then:
			1 * joinStep.process(_)
			1 * leaveStep.process(_)
			1 * replyStep.process(_)
	}
}
