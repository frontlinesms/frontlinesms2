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

	private void setupDataAndAssert(boolean flag, Integer max, Integer offset, Closure closure, status=MessageStatus.SENT)  {
		registerMetaClass(Fmessage)
		Fmessage.metaClass.'static'.hasFailedMessages = { -> return true}
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
		assert results['pollInstanceList'].every {!it.archived}
		assert results['hasFailedMessages']
    }
}
