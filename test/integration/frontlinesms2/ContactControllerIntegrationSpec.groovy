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
		Group.findAll()*.delete(flush:true, failOnError:true)
		Contact.findAll()*.delete(flush:true, failOnError:true)
	}

	def makeGroupMember() {
		c.addToGroups(g)
		c.save(flush: true)
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
	
	def "should move multiple selected contacts to the same group" () {
		given:
			def contact1 = new Contact(name: "Test 1").save(failOnError: true)
			def contact2 = new Contact(name: "Test 2").save(failOnError: true)
		when:
			controller.params.contactIds = "${contact1.id}, ${contact2.id}"
			controller.params.groupsToAdd = ",${g.id},"
			controller.params.groupsToRemove = ","
			controller.updateMultipleContacts()
		then:
			g.getMembers() == [contact1, contact2]
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
	
	def "Deleting a contact removes it from the database" () {
		when:
			controller.params.contactId = c.id
			controller.deleteContact()
		then:
			!Contact.findAllByName('Bob')
	}
	
	def "Deleting a contact removes it from a contact group" () {
		given: 
			c.addToGroups(g)
			assert g.getMembers()
		when:
			controller.params.contactId = c.id
			controller.deleteContact()
			g.refresh()
		then:
			!Contact.findByName('Bob')
			!g.getMembers()
	}
	
	def "should return list of shared groups and non shared groups for multiple contacts" () {
		given: 
			def contact1 = new Contact(name: "Lisa").save(failOnError: true)
			def contact2 = new Contact(name: "Samantha").save(failOnError: true)
			def group1 = new Group(name: "Test group 1").save(failOnError: true)
			def group2 = new Group(name: "Test group 2").save(failOnError: true)
			contact1.addToGroups(group1)
			contact1.addToGroups(group2)
			contact1.addToGroups(g)
			contact2.addToGroups(group1)
			contact2.addToGroups(group2)
		when:
			controller.params.contactIds = "${contact1.id}, ${contact2.id}"
			controller.multipleContactGroupList()
			def model = controller.modelAndView.model
		then:
			model.sharedGroupInstanceList == [group1, group2]
			model.nonSharedGroupInstanceList == [g]
	}
}
