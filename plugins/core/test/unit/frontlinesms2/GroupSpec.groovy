package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(Group)
@Mock([Contact, GroupMembership])
class GroupSpec extends Specification {
	def setup() {
		Group.metaClass.getMembers = {
			GroupMembership.findAllByGroup(delegate)*.contact.unique().sort { it.name }
		}
	}

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
		then:
			!noNameGroup.validate()
			namedGroup.validate()
	}

	def "group must have unique name"() {
		when:
			def name1Group = new Group(name:'Same').save()
			def name2Group = new Group(name:'Same')
		then:
			name1Group.validate()
			!name2Group.validate()
	}

	def "group name must be less than 255 characters"() {
		when:
			def longNameGroup = new Group(name:'0123456789abcdef'*16)
		then:
			!longNameGroup.validate()
	}

	def "should get all the member addresses for a group"() {
		setup:
			def group = new Group(name: "Sahara")
			mockDomain Group, [group]
			mockDomain GroupMembership, [new GroupMembership(group: group, contact: new Contact(mobile: "12345")),
				new GroupMembership(group: group, contact: new Contact(mobile: "56484"))]
		when:
			def result = group.addresses
		then:
			result.containsAll(["12345", "56484"])

	}

	def "should list all the group names with a count of number of people in the group"() {
		setup:
			def sahara = new Group(name: "sahara").save()
			def thar = new Group(name: "thar").save()
			[['Bob', 'address1'], ['Jim', 'address2']].each {
				Contact c = new Contact(name:it[0], mobile:it[1]).save()
				GroupMembership.create(c, sahara)
			}
			Contact kate = new Contact(name: "Kate", mobile: "address3")
			GroupMembership.create(kate, thar)
			
		when:
			def result = Group.groupDetails
		then:
			result["group-$sahara.id"] == [name:"sahara",addresses:["address1", "address2"]]
			result["group-$thar.id"] == [name:"thar",addresses: ["address3"]]
	}
}

