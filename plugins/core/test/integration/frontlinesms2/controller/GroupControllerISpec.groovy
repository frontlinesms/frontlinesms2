package frontlinesms2.controller

import frontlinesms2.*

class GroupControllerISpec extends grails.plugin.spock.IntegrationSpec {

	def controller
	
	def setup(){
		controller = new GroupController()
	}
	
	def "can update group name"() {
		setup:
			new Group(name:'groupName').save(failOnError:true, flush:true)
			def group = Group.findByName("groupName")
			controller.params.id = group.id
			controller.params.name = "renamed group"
		when:
			controller.update()
			def updatedGroup = Group.get(group.id)
		then:
			updatedGroup.name == "renamed group"
			controller.response.redirectedUrl == "/contact/show?groupId=${group.id}"
	}
	
}
