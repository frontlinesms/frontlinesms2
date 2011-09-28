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
			assertRedirectArgs()
	}

	def "should error for invalid subscription keywords"() {
		setup:
			mockDomain(Group, [new Group(name: 'windows', subscriptionKey: "JOIN",
										id: 1l, unsubscriptionKey: "UNJOIN"),
								new Group(id: 2L)])
			mockParams.id = 2L
			mockParams.subscriptionKey = "JOIN"
			mockParams.unsubscriptionKey = "REMOVE"
		when:
			controller.update()
		then:
			controller.flash.message == "Group not saved successfully"
			assertRedirectArgs()
	}

	def "should not error when a group which already has subscription keywords is updated"() {
		setup:
			mockDomain(Group, [new Group(name: 'windows', subscriptionKey: "JOIN",
										id: 1L, unsubscriptionKey: "UNJOIN")])
			mockParams.id = 1L
			mockParams.subscriptionKey = "JOIN"
			mockParams.unsubscriptionKey = "REMOVE"
		when:
			controller.update()
		then:
			controller.flash.message == "Group updated successfully"
			assertRedirectArgs()
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
