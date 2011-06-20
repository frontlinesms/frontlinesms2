package frontlinesms2

import grails.plugin.spock.*

class SearchControllerSpec extends ControllerSpec{	
	def "default action is 'list'"() {
		when:
			controller.index()
		then:
			!controller.redirectArgs.controller || controller.redirectArgs.controller == 'search'
			controller.redirectArgs.action == 'list'
	}	
}
