package frontlinesms2

import grails.plugin.spock.*

class MessageControllerSpec extends ControllerSpec {
	MessageSendService mockMessageSendService

	def setup() {
		mockDomain Contact
		mockDomain Fmessage
		registerMetaClass(Fmessage)
		registerMetaClass(Contact)
		Contact.metaClass.'static'.withNewSession = {closure -> closure.call()}
		mockParams.messageText = "text"
		mockParams.max = 10
		mockParams.offset = 0
		mockParams.starred = false
	    controller.messageSendService = new MessageSendService()

		def sahara = new Group(name: "Sahara")
		def thar = new Group(name: "Thar")
		mockDomain Group, [sahara, thar]
		mockDomain GroupMembership, [new GroupMembership(group: sahara, contact: new Contact(primaryMobile: "12345")),
			new GroupMembership(group: sahara, contact: new Contact(primaryMobile: "56484")),
			new GroupMembership(group: thar, contact: new Contact(primaryMobile: "12121")),
			new GroupMembership(group: thar, contact: new Contact(primaryMobile: "22222"))]

		controller.metaClass.getPaginationCount = {-> return 10}
		mockMessageSendService = Mock()
		controller.messageSendService = mockMessageSendService
	}

	def "should send message to all the members in a group"() {
		setup:
			mockParams.groups = "Sahara"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			1 * mockMessageSendService.send {it.dst == "12345"}
			1 * mockMessageSendService.send {it.dst == "56484" }
	}

	def "should send message to all the members in multiple groups"() {
		setup:
			mockParams.groups = ["Sahara", "Thar"]
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			1 * mockMessageSendService.send {it.dst == "12345" }
			1 * mockMessageSendService.send {it.dst == "56484" }
			1 * mockMessageSendService.send {it.dst == "12121" }
			1 * mockMessageSendService.send {it.dst == "22222" }
	}

	def "should send a message to the given address"() {
		setup:
			mockParams.addresses = "+919544426000"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			1 * mockMessageSendService.send {it.dst == "+919544426000" }
	}

	def "should resend multiple failed message"() {
		setup:
			mockDomain(Fmessage, [new Fmessage(id: 1L), new Fmessage(id: 2L), new Fmessage(id: 3L)])
			mockParams.failedMessageIds = [1, 2]
		when:
			controller.send()
		then:
			1 * mockMessageSendService.send {it.id == 1L}
			1 * mockMessageSendService.send {it.id == 2L}
	}

	def "should resend a single failed message"() {
		setup:
			mockDomain(Fmessage, [new Fmessage(id: 1L), new Fmessage(id: 2L), new Fmessage(id: 3L)])
			mockParams.failedMessageIds = "1"
		when:
			controller.send()
		then:
			1 * mockMessageSendService.send {it.id == 1L}
	}

	def "should eliminate duplicate address if present"() {
		setup:
			mockParams.addresses = "12345"
			mockParams.groups = "Sahara"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			1 * mockMessageSendService.send {it.dst == "12345" }
			1 * mockMessageSendService.send {it.dst == "56484" }
	}

	def "should send message to each recipient in the list of address"() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			mockParams.addresses = addresses
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			1 * mockMessageSendService.send {it.dst == "+919544426000" }
			1 * mockMessageSendService.send {it.dst == "+919004030030" }
			1 * mockMessageSendService.send {it.dst == "+1312456344" }
	}

	def "should display flash message on successful message sending"() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			mockParams.addresses = addresses
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			controller.flash.message == "Message has been queued to send to +919544426000, +919004030030, +1312456344"

	}

     private void setupDataAndAssert(boolean flag, Integer max, Integer offset, Closure closure, status=MessageStatus.SENT)  {
		registerMetaClass(Fmessage)
		Fmessage.metaClass.'static'.hasUndeliveredMessages = { -> return true}
		def fmessage = new Fmessage(id:1L, src: "src1", starred: flag, status: status)
		mockDomain Folder
		mockDomain Poll, [new Poll(archived: true), new Poll(archived: false)]
		mockDomain Contact
		mockDomain RadioShow
		mockParams.starred = flag
		mockParams.failed = flag
		mockParams.max = max
		mockParams.offset = offset
		mockDomain Fmessage, [fmessage]

		def results = closure.call(fmessage)

		assert results['messageInstanceList'] == [fmessage]
		assert results['messageInstanceTotal'] == 2
		assert results['messageInstance'] == fmessage
		assert results['messageInstanceList']*.contactExists == [false]
		assert results['messageInstanceList']*.contactExists == [false]
		assert results['pollInstanceList'].every {!it.archived}
		assert results['hasUndeliveredMessages']
    }
}
