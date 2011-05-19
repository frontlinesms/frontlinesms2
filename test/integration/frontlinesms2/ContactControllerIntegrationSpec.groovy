package frontlinesms2

class ContactControllerIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def "adding a contact to a group multiple times leads to a successful add request"() {
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
			Contact.get(c.id).isMemberOf(g)
	}	
	
	def "removing a contact from a group multiple times leads to a successful remove"() {
		given:
			def controller = new ContactController()
			def c = new Contact(name:'Ada').save(failOnError:true)
			def g = new Group(name:'test group').save(failOnError:true)
			c.addToGroups(g, true)
			assert(Contact.get(c.id).isMemberOf(Group.get(g.id)))
			
			controller.params.id = c.id
			controller.params.groupsToAdd = ","
			controller.params.groupsToRemove = ",${g.id},${g.id},"
		when:
			controller.update()
		then:
			!Contact.get(c.id).isMemberOf(Group.get(g.id))
	}
}