package frontlinesms2.controller

import spock.lang.*
import frontlinesms2.*
import grails.plugin.spock.*

class QuickMessageControllerISpec extends IntegrationSpec {
	def controller
	
	def setup() {
		controller = new QuickMessageController()
	}
	
	def "contact list returned should be sorted alphabetically"() {
		given:
			def contact2 = Contact.build(name:'Charlie')
			def contact3 = Contact.build(name:'Alice')
		when:
			def model = controller.create()
		then:
			model.contactList == [contact3, contact2]
	}
	
	def "list of smart groups should be included in the group list"() {
		given:
			def s = SmartGroup.build(name:'English numbers', mobile:'+44')
		when:
			def model = controller.create()
		then:
			model.groupList["smartgroup-$s.id"]?.name == "English numbers"
			model.groupList["smartgroup-$s.id"]?.addresses == []
	}
}
