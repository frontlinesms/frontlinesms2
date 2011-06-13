package frontlinesms2

import grails.plugin.spock.*

class SearchControllerSpec extends ControllerSpec{
	
	def "default action is 'show'"() {
		when:
			controller.index()
		then:
			controller.redirectArgs.controller == 'connection' || !controller.redirectArgs.controller
			controller.redirectArgs.action == 'list'
	}
	
	def "messageContent search operation returns a valid response for any given keyword"() {
		
	}
}

