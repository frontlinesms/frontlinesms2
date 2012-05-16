package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

@TestFor(ContactController)
@Mock([Contact, Group, GroupMembership])
class ContactControllerSpec extends Specification {
	def setup() {
		// Not sure why this is necessary with Test Mixins, but it seems to be:
		Contact.metaClass.static.getAll = { ids ->
			return Contact.findAll().find { it.id in ids } }
		Group.metaClass.static.getAll = { ids ->
			return Group.findAll().find { it.id in ids } }
	}

	def "should render model containing shared and unshared groups"() {
		setup:
			def alice = new Contact(name:"Alice", mobile:"12345").save()
			def bob = new Contact(name:"Bob", mobile:"54321").save()
			def group1 = new Group(name:"group1").save()
			def group2 = new Group(name:"group2").save()
			Group.metaClass.static.createCriteria = { [list: { Closure c -> [group1] }] }
			[new GroupMembership(group:group1, contact: alice),
					new GroupMembership(group:group1, contact: bob) ,
					new GroupMembership(group:group2, contact: bob)]*.save()
			params.checkedContactList = "$alice.id,$bob.id"
		when:
			controller.multipleContactGroupList()
		then:
			model.sharedGroupInstanceList == [group1]
			model.nonSharedGroupInstanceList == [group2]
	}
}

