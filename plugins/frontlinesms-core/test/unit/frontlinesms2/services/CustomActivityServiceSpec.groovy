package frontlinesms2.services

import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*

@TestFor(CustomActivityService)
@Mock([])
class CustomActivityServiceSpec extends Specification {
	
	def "triggerSteps() invokes appropriate do action on various services"() {
		given:
			def joinStep = Mock(JoinActionStep)
			def leaveStep = Mock(LeaveActionStep)
			def replyStep = Mock(ReplyActionStep)
			def c = Mock(CustomActivity)
			c.steps >> [joinStep, leaveStep, replyStep]
		when:
			c.triggerSteps()
		then:

	}
}
