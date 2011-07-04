package frontlinesms2

import grails.plugin.spock.ControllerSpec

class QuickMessageControllerSpec extends ControllerSpec {

	def setup() {
		def jim = new Contact(name:"jim", address:"12345")
		def mohave = new Group(name:"Mojave", members: [jim])
		mockDomain Contact, [jim]
		mockDomain Group, [mohave]

	}

	def 'create returns the contact, group list'() {
		setup:
			def address= "9544426444"
			mockParams.recipient =  address
		when:
			def result = controller.create()
		then:
			def jim = Contact.findByName('jim')
			result['contactList'] == [jim]
			result['groupList'] == ["Mojave":1]
			result['recipients'] ==  [address]
			result['nonExistingRecipients'] ==  [address]
	}

	def "should identify existing contacts and non existing recipients"() {
		setup:
			def address = "12345"
			mockParams.recipient =  address
		when:
			def result = controller.create()
		then:
			result['recipients'] ==  [address]
			result['nonExistingRecipients'] ==  []
	}

	def "recipient list must be empty if there is no need for pre populating address"() {
		when:
			def result = controller.create()
		then:
			!result['recipients']
			!result['nonExistingRecipients']
	}

}