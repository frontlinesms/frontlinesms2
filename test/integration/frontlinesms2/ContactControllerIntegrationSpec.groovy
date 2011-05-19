package frontlinesms2

class ContactControllerIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def "adding a contact to a group multiple times only leads to a single add request"() {
		given:
			def controller = new ContactController()
			def c = new Contact(name:'Ada').save(failOnError:true)
			def g = new Group(name:'test group').save(failOnError:true)
			controller.params.id = c.id
			controller.params.groupsToAdd = ",${g.id},${g.id},"
			controller.params.groupsToRemove = ","
		when:
			controller.update()
		then:
			1 * c.addToGroups(g)
	}
}