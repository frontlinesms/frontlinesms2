package frontlinesms2

class GroupIntegrationSpec extends grails.plugin.spock.IntegrationSpec {

	def "should check for uniqueness across subscription and unsubscription keywords"() {
		setup:
			assert Group.count() == 0
			new Group(name: "Group1", subscriptionKey: "ADD", unsubscriptionKey: "REMOVE").save(failOnError: true, flush: true)
			new Group(name: "Group2",  subscriptionKey: "ADD1", unsubscriptionKey: "REMOVE1").save(failOnError: true, flush: true)
		when:
			assert Group.count() == 2
			def invalid1 = new Group(name: "group4", subscriptionKey: "ADD", unsubscriptionKey: "REJECT")
			def invalid2 = new Group(name: "group5", subscriptionKey: "ACCEPT", unsubscriptionKey: "REMOVE")
			def invalid3 = new Group(name: "group6", subscriptionKey: "ADD1", unsubscriptionKey: "REMOVE")
			def invalid4 = new Group(name: "group7", subscriptionKey: "REMOVE1", unsubscriptionKey: "ADD1")
			def invalid5 = new Group(name: "group8", subscriptionKey: "ADDGROUP", unsubscriptionKey: "ADDGROUP")

			def valid1 = new Group(name: "group9", unsubscriptionKey: "unsubkey")
			def valid2 = new Group(name: "group10", subscriptionKey: "subkey")

		then:
			!invalid1.validate()
			!invalid2.validate()
			!invalid3.validate()
			!invalid4.validate()
			!invalid5.validate()

			valid1.validate()
			valid2.validate()
	}
	
	def "should search for contacts with similar names" () {
		setup:
			def fpGroup = new Group(name: "Functional programming").save(failOnError: true, flush: true)
			def samAnderson = new Contact(name: 'Sam Anderson', primaryMobile: "1234567891").save(failOnError: true)
			def samJones = new Contact(name: 'SAm Jones', primaryMobile: "1234567892").save(failOnError: true)
			def samTina = new Contact(name: 'SaM Tina', primaryMobile: "1234567893").save(failOnError: true)
			samAnderson.addToGroups(fpGroup, true)
			samJones.addToGroups(fpGroup,true)
			def bob = new Contact(name: 'Bob', primaryMobile: "1234567894").save(failOnError: true).addToGroups(fpGroup,true)
		when:
			def results = GroupMembership.searchForContacts([groupName:fpGroup.name,contactName:"Sam"])
		then:
			assert results == [samAnderson, samJones]
		when:
			results = GroupMembership.searchForContacts([contactName:"Sam"])
		then:
			assert results == [samAnderson, samJones, samTina]
	}
}
