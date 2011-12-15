package frontlinesms2

import grails.plugin.spock.ControllerSpec

class GroupControllerSpec extends ControllerSpec {

	def "should list all the groups in the system"() {
		setup:
			mockDomain(Group, [new Group(name: 'windows'), new Group(name: 'Mac')])
		when:
			def results = controller.list()
		then:
			results['groups']*.name.containsAll(['windows', 'Mac'])
	}

	def "test group show"() {
		setup:
			mockParams.id = 3L
		when:
			controller.show()
		then:
			assert controller.redirectArgs.controller == 'contact'
			assert controller.redirectArgs.action == 'show'
			assert controller.redirectArgs.params.groupId == 3L

	}

	private def assertRedirectArgs() {
		assert controller.redirectArgs.controller == 'message'
		assert controller.redirectArgs.action == 'inbox'
		true
	}


}
