package frontlinesms2

import grails.plugin.spock.ControllerSpec

class SubscriptionControllerSpec extends ControllerSpec {

	def "should list all the groups in the system"() {
		setup:
			mockDomain(Group, [new Group(name: 'windows'), new Group(name: 'Mac')])
		when:
			def results = controller.create()
		then:
			results['groups']*.name.containsAll(['windows', 'Mac'])
	}

}