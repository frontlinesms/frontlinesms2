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
			mockDomain(Fmessage,
					[new Fmessage(id: 1L, date: new Date(), inbound: false, hasFailed: true, dispatches: [new Dispatch(dst:"234", status: DispatchStatus.FAILED)]),
				new Fmessage(id: 2L, date: new Date(), inbound: false, hasFailed: true, dispatches: [new Dispatch(dst:"234", status: DispatchStatus.FAILED)]),
				new Fmessage(id: 3L, date: new Date(), inbound: false, hasFailed: true, dispatches: [new Dispatch(dst:"234", status: DispatchStatus.FAILED)])])
			mockParams.failedMessageIds = [1, 2]
		when:
			controller.retry()
		then:
			1 * mockMessageSendService.send {it.id == 1L}
			1 * mockMessageSendService.send {it.id == 2L}
	}

	def "should resend a single failed message"() {
		setup:
			mockDomain(Fmessage,
					[new Fmessage(id: 1L, date: new Date(), inbound: false, hasFailed: true, dispatches: [new Dispatch()]),
				new Fmessage(id: 2L, date: new Date(), inbound: false, hasFailed: true, dispatches: [new Dispatch()]),
				new Fmessage(id: 3L, date: new Date(), inbound: false, hasFailed: true, dispatches: [new Dispatch()])])
			mockParams.failedMessageIds = "1"
		when:
			controller.retry()
		then:
			1 * mockMessageSendService.send {it.id == 1L}
	}
}
