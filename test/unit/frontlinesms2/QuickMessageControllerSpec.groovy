package frontlinesms2

import grails.plugin.spock.ControllerSpec

class QuickMessageControllerSpec extends ControllerSpec {
	def 'create returns the contact and group list'() {
		setup:
			def jim = new Contact(name:"jim", address:"12345")
			def mohave = new Group(name:"Mojave")
			def membership = new GroupMembership(group: mohave, contact: jim)
			mockDomain Contact, [jim]
			mockDomain GroupMembership, [membership]
		when:
			def result = controller.create()
		then:
			result['contactList'] == [jim]
			result['groupList'] == ["Mojave":[membership]]
	}
}