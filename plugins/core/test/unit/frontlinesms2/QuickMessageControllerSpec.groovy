package frontlinesms2

import grails.plugin.spock.ControllerSpec

class QuickMessageControllerSpec extends ControllerSpec {

	def setup() {
		def jim = new Contact(name:"jim", mobile:"12345")
		def mohave = new Group(name:"Mojave")
		mockDomain GroupMembership, [new GroupMembership(group: mohave, contact: jim)]
		mockDomain Contact, [jim]
		mockDomain Group, [mohave]
		mockDomain SmartGroup, []
	}

	def 'create returns the contact, group list'() {
		setup:
			def jim = Contact.findByName('jim')
			def mohave = Group.findByName('Mojave')
			def address= ["9544426444"]
			mockParams.recipients = address
		when:
			def result = controller.create()
			println "result.groupList: ${result.groupList['group-32']*.class}"
		then:
			result['contactList'] == [jim]
			result['recipients'] == address
			result['nonExistingRecipients'] == address
			result['groupList']["group-$mohave.id"] == [jim.mobile]
	}

	def 'create returns the contact, group list even if address comes as a string'() {
		setup:
			def address= "9544426444"
			mockParams.recipients = address
		when:
			def result = controller.create()
		then:
			result['recipients'] == [address]
			result['nonExistingRecipients'] == [address]
	}

	def "should identify existing contacts and non existing recipients"() {
		setup:
			def address = ["12345"]
			mockParams.recipients = address
		when:
			def result = controller.create()
		then:
			result['recipients'] == address
			result['nonExistingRecipients'] ==  []
			result['configureTabs'] ==  ['tabs-1', 'tabs-2', 'tabs-3', 'tabs-4']
	}

	def "should set configure tabs when set in the incoming request"() {
		setup:
			mockParams.configureTabs = 'tabs-1 , tabs-3'
		when:
			def result = controller.create()
		then:
			result['configureTabs'] ==  ['tabs-1', 'tabs-3']
	}

	def "recipient list must be empty if there is no need for pre populating address"() {
		when:
			def result = controller.create()
		then:
			!result['recipients']
			!result['nonExistingRecipients']
	}

}
