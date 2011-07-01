package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class PollControllerSpec extends ControllerSpec {

	def "default action is CREATE"() {
		when:
			controller.index()
		then:
			controller.redirectArgs.controller == 'poll' || !controller.redirectArgs.controller
			controller.redirectArgs.action == 'create'
	}
}

