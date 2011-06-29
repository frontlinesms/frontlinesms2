package frontlinesms2

import grails.plugin.spock.*

class MessageControllerSpec extends ControllerSpec {

	def setup() {
		mockDomain Fmessage
		mockParams.messageText = "text"
		controller.messageSendService = new MessageSendService()
		def sahara = new Group(name: "Sahara")
		def thar = new Group(name: "Thar")
		mockDomain Group, [sahara, thar]
		mockDomain GroupMembership, [new GroupMembership(group: sahara, contact: new Contact(address: "12345")),
			new GroupMembership(group: sahara, contact: new Contact(address: "56484")),
			new GroupMembership(group: thar, contact: new Contact(address: "12121")),
			new GroupMembership(group: thar, contact: new Contact(address: "22222"))]
	}

	def "should send message to all the members in a group"() {
		setup:
			mockParams.groups = "Sahara"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.list()*.dst == ["12345","56484"]
	}

	def "should send message to all the members in multiple groups"() {
		setup:
			mockParams.groups = ["Sahara", "Thar"]
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.list()*.dst == ["12345","56484","12121","22222"]
	}
	
	def "should send a message to the given address"() {
		setup:
			mockParams.addresses = "+919544426000"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.count() == 1
	}

	def "should eliminate duplicate address if present"() {
		setup:
			mockParams.addresses = "12345"
			mockParams.groups = "Sahara"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.count() == 2
	}

	def "should send message to each recipient in the list of address"() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			mockParams.addresses = addresses
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.list()*.dst == addresses
			Fmessage.count() == 3
	}

	def "should fetch pending messages"() {
		setup:
			registerMetaClass(Fmessage)
			def pendingMessages = [new Fmessage(src: "src"), new Fmessage(src: "src")]
			Fmessage.metaClass.'static'.getPendingMessages = {->
				pendingMessages
			}
			mockDomain(Folder)
			mockDomain(Poll)
			mockDomain(Contact)
		when:
			def results = controller.pending()
		then:
			results['messageInstanceList'] == pendingMessages
			results['messageSection'] == 'pending'
			results['messageInstanceTotal'] == 2
			results['messageInstance'] == pendingMessages[0]
			results['messageInstanceList']*.contactExists == [false, false]
			results['messageInstanceList']*.displaySrc == ["src", "src"]
	}
}