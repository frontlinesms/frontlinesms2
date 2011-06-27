package frontlinesms2

class ContactControllerIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def c
	def g

	def setup() {
		controller = new ContactController()
		c = new Contact(name:'Bob').save(failOnError:true)
		g = new Group(name:'test group').save(failOnError:true)
	}

	def cleanup() {
		Contact.findAll()*.delete(flush:true, failOnError:true)
		Group.findAll()*.delete(flush:true, failOnError:true)
	}

	def makeGroupMember() {
		c.addToGroups(g, true)
		assert(Contact.get(c.id).isMemberOf(Group.get(g.id)))
	}

	def "adding a contact to a group multiple times leads to a successful add request"() {
		given:
			controller.params.contactId = c.id
			controller.params.groupsToAdd = ",${g.id},${g.id},"
			controller.params.groupsToRemove = ","
			controller.params.fieldsToAdd = ","
			controller.params.fieldsToRemove = ","
		when:
			controller.update()
		then:
			Contact.get(c.id).isMemberOf(g)
	}	
	
	def "removing a contact from a group multiple times leads to a successful remove"() {
		given:
			makeGroupMember()
			controller.params.contactId = c.id
			controller.params.groupsToAdd = ","
			controller.params.groupsToRemove = ",${g.id},${g.id},"
			controller.params.fieldsToAdd = ","
			controller.params.fieldsToRemove = ","
		when:
			controller.update()
		then:
			!Contact.get(c.id).isMemberOf(Group.get(g.id))
	}

	def 'when showing all contacts, the first contact in the list is selected if none is specified'() {
		given:
			controller.params.contactId = null
		when:
			controller.list()
		then:
			controller.response.redirectedUrl == "/contact/show/${c.id}?max=10&sort=name"
	}

	def 'when showing a group, the first contact in the group is selected if none is specified'() {
		given:
			makeGroupMember()
			controller.params.groupId = g.id
			controller.params.contactId = null
		when:
			controller.list()
		then:
			controller.response.redirectedUrl == "/group/show/${g.id}/contact/show/${c.id}?max=10&sort=name"
	}

	def "adding and removing a contact from the same group triggers error"() {
		given:
			controller.params.contactId = c.id
			controller.params.groupsToAdd = ",g,"
			controller.params.groupsToRemove = ",g,"
			controller.params.fieldsToAdd = ","
			controller.params.fieldsToRemove = ","
		when:
			controller.update()
			def model = controller.show()
		then:
			model.contactInstance.errors
	}

	def "All contacts list appears in alphabetical order"() {
		setup:
			def contact2 = new Contact(name:'Charlie').save(failOnError:true)
			def contact3 = new Contact(name:'Alice').save(failOnError:true)
			controller.params.contactId = c.id
			controller.params.groupsToAdd = ","
			controller.params.groupsToRemove = ","
			controller.params.fieldsToAdd = ","
			controller.params.fieldsToRemove = ","
			assert controller.show() != null
		when:
			def model = controller.show()
		then:
			model.contactInstanceList == [contact3, c, contact2]
			model.contactInstanceList != [c, contact2, contact3]
	}
}
