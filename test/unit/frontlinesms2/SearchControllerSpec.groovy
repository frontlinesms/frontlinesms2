package frontlinesms2

import grails.plugin.spock.*

class SearchControllerSpec extends ControllerSpec{
	
	def "default action is 'show'"() {
		when:
			controller.index()
		then:
			controller.redirectArgs.controller == 'search' || !controller.redirectArgs.controller
			controller.redirectArgs.action == 'show'
	}
	
	
}

