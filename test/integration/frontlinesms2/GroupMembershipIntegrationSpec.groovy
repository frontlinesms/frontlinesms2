package frontlinesms2

class GroupMembershipIntegrationSpec extends grails.plugin.spock.IntegrationSpec {

	def "should list all the group names with a count of number of people in the group"() {
		setup:
			def sahara = new Group(name: "sahara").save(flush: true)
			def thar = new Group(name: "thar").save(flush: true)
			def contact1 = new Contact(name: "Bob", address: "address1").save(flush: true)
			def contact2 = new Contact(name: "Jim", address: "address2").save(flush: true)
			def contact3 = new Contact(name: "Kate", address: "address3").save(flush: true)
			sahara.addToMembers(contact1)
			sahara.addToMembers(contact2)
		    thar.addToMembers(contact3)
		when:
			def result = GroupMembership.getGroupDetails()
		then:
			result['sahara'].size == 2
			result['thar'].size == 1
	}

}
