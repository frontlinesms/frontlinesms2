package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(GroupController)
@Mock(Group)
class GroupControllerSpec extends Specification {
	def "should list all the groups in the system"() {
		setup:
			[new Group(name: 'windows'), new Group(name: 'Mac')]*.save()
		when:
			def results = controller.list()
		then:
			results.groups*.name.sort() == ['Mac', 'windows']
	}

	def "test group show"() {
		setup:
			params.id = 3L
		when:
			controller.show()
		then:
			response.redirectUrl == '/contact/show/3?groupId=3' // this should not have standard 'id' param set - please investigate and fix
	}
}

