package frontlinesms2

import grails.plugin.spock.UnitSpec
import grails.plugin.spock.ControllerSpec

class QuickMessageControllerSpec extends ControllerSpec {
	def 'create returns the contact and group list'() {
		setup:
			def contacts = [new Contact(name: "jim", address: "12345")]
			def groups = [new Group(name: "group")]
			mockDomain Contact, contacts
			mockDomain Group, groups
		when:
			def result = controller.create()
		then:
			result['contactList'] == contacts
			result['groupList'] == groups
	}
}