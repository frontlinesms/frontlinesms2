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

	def "should update a group with subscription keywords"() {
		setup:
			mockDomain(Group, [new Group(name: 'windows', id: 2L)])
			mockParams.id = 2L
			mockParams.subscriptionKey = "JOIN"
			mockParams.unsubscriptionKey = "REMOVE"
		when:
			controller.update()
		then:
			def group = Group.get(2L)
			group.subscriptionKey == "JOIN"
			group.unsubscriptionKey == "REMOVE"
			controller.flash.message == "Group updated successfully"
	}
}