package frontlinesms2

class ContactControllerIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def c
	def g

	def setup() {
		controller = new ContactController()
		c = new Contact(name:'Ada').save(failOnError:true)
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
//
//	def "when a new group is saved, user is redirected to the group's show page"() {
//		given:
//			controller.params.name = 'new group'
//			assert Group.count() == 1
//		when:
//			controller.saveGroup()
//		then:
//			controller.response.redirectedUrl =~ /\/group\/show\/\d/
//			Group.count() == 2
//	}

	def "when a new contact is saved, user is redirected to the group's show page"() {
		given:
			controller.params.name = 'new contact'
			controller.params.address = '1234565'
			controller.params.groupsToAdd = ","
			controller.params.groupsToRemove = ","
			assert Contact.count() == 1
		when:
			controller.saveContact()
		then:
			controller.response.redirectedUrl =~ /\/contact\/show\/\d/
			Contact.count() == 2
	}
}
