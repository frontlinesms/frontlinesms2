package frontlinesms2.domain

import frontlinesms2.*

class GroupISpec extends grails.plugin.spock.IntegrationSpec {
	def contactSearchService
	
	def "should search for contacts with similar names" () {
		setup:
			def fpGroup = new Group(name: "Functional programming").save(failOnError: true, flush: true)
			def samAnderson = new Contact(name: 'Sam Anderson', mobile: "1234567891").save(failOnError: true)
			def samJones = new Contact(name: 'SAm Jones', mobile: "1234567892").save(failOnError: true)
			def samTina = new Contact(name: 'SaM Tina', mobile: "1234567893").save(failOnError: true)
			samAnderson.addToGroups(fpGroup, true)
			samJones.addToGroups(fpGroup,true)
			def bob = new Contact(name: 'Bob', mobile: "1234567894").save(failOnError: true).addToGroups(fpGroup,true)
		when:
			def results = contactSearchService.getContacts([groupId:fpGroup.id, searchString:"Sam", max:50, offset:0])
			def resultsCount = contactSearchService.countContacts([groupId:fpGroup.id, searchString:"Sam"])
		then:
			assert results == [samAnderson, samJones]
			assert resultsCount == 2
		when:
			results = contactSearchService.getContacts([searchString:"Sam", max:50, offset:0])
			resultsCount = contactSearchService.countContacts([searchString:"Sam"])			
		then:
			assert results == [samAnderson, samJones, samTina]
			assert resultsCount == 3
	}

	def "should be able to get list of shared and non-shared groups"() {
		setup:
			def alice = Contact.build()
			def bob = Contact.build()
			def group1 = Group.build()
			def group2 = Group.build()
			alice.addToGroup(group1)
			bob.addToGroup(group1)
			bob.addToGroup(group2)
			def contactIds = [alice.id, bob.id]
		expect:
			Group.getGroupLists(contactIds) == [shared:[group1], nonShared:[group2]]
	}
}

