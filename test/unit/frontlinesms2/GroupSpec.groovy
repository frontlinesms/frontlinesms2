package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class GroupSpec extends UnitSpec {
	def "group may have a name"() {
		when:
			Group g = new Group()
			assert g.name == null
			g.name = 'People'
		then:
			g.name == 'People'
	}

	def "group must have a name"() {
		when:
			def noNameGroup = new Group()
			def namedGroup = new Group(name:'People')
			mockForConstraintsTests(Group, [noNameGroup, namedGroup])
		then:
			!noNameGroup.validate()
			namedGroup.validate()
	}

	def "group must have unique name"() {
		when:
			def name1Group = new Group(name:'Same')
			def name2Group = new Group(name:'Same')
			mockForConstraintsTests(Group, [name1Group, name2Group])
		then:
			!name1Group.validate()
			!name2Group.validate()
	}

	def "group name must be less than 255 characters"() {
		when:
			def longNameGroup = new Group(name:'0123456789abcdef'*16)
			mockForConstraintsTests(Group, [longNameGroup])
		then:
			assert longNameGroup.name.length() > 255
			!longNameGroup.validate()
	}

	def "should get all the member addresses for a group"() {
		setup:
			def group = new Group(name: "Sahara")
			mockDomain Group, [group]
			mockDomain GroupMembership, [new GroupMembership(group: group, contact: new Contact(primaryMobile: "12345")),
				new GroupMembership(group: group, contact: new Contact(primaryMobile: "56484"))]
		when:
			def result = group.getAddresses()
		then:
			result.containsAll(["12345", "56484"])

	}

	def "should list all the group names with a count of number of people in the group"() {
		setup:
			def sahara = new Group(name: "sahara")
			def thar = new Group(name: "thar")
			mockDomain(Group, [sahara, thar])
			mockDomain GroupMembership, [new GroupMembership(group: sahara, contact: new Contact(name: "Bob", primaryMobile: "address1")), new GroupMembership(group: sahara, contact: new Contact(name: "Jim", primaryMobile: "address2")),
				new GroupMembership(group: thar, contact: new Contact(name: "Kate", primaryMobile: "address3"))]
			
		when:
			def result = Group.getGroupDetails()
		then:
			result.sahara == ["address1", "address2"]
			result.thar == ["address3"]
	}

	def "should reject special characters for subscription keys"() {
		setup:
			mockDomain(Group)
		when:
			def group = new Group(name: "name", subscriptionKey: "sub@key", unsubscriptionKey: "unsubkey")
		then:
			!group.validate()
	}
}

