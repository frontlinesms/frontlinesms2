package frontlinesms2.controllers

import frontlinesms2.*

import grails.test.mixin.*
import spock.lang.*

@TestFor(QuickMessageController)
@Mock([GroupMembership, Contact, Group, SmartGroup, TextMessage])
class QuickMessageControllerSpec extends Specification {

	def setup() {
		def jim = new Contact(name:"jim", mobile:"12345").save()
		def mohave = new Group(name:"Mojave").save()
		GroupMembership.create(jim, mohave)
		Group.metaClass.getMembers = {
			GroupMembership.findAllByGroup(delegate)*.contact.unique().sort { it.name }
		}
	}

	def "create returns the prepopulated recipients based on params.contactId"() {
		setup:
			def contact = Contact.findByName("jim")
			params.contactId = "${contact.id}"
		when:
			def result = controller.create()
		then:
			result['addresses'] == [contact.mobile]
			result['recipientName'] == 'jim'
			result['configureTabs'] ==  ['tabs-1', 'tabs-2', 'tabs-3', 'tabs-4']
	}

	def "should set configure tabs when set in the incoming request"() {
		setup:
			params.configureTabs = 'tabs-1 , tabs-3'
		when:
			def result = controller.create()
		then:
			result['configureTabs'] ==  ['tabs-1', 'tabs-3']
	}

	def "addresses list must be empty if there is no need for pre populating address"() {
		when:
			def result = controller.create()
		then:
			!result['addresses']
	}

}
