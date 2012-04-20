package frontlinesms2

import grails.plugin.spock.*

import static frontlinesms2.DispatchStatus.*

class MessageControllerSpec extends ControllerSpec {
	MessageSendService mockMessageSendService

	def setup() {
		registerMetaClass MessageController
		controller.metaClass.message = { Map args -> args.code }
		controller.metaClass.getPaginationCount = { -> 10 }
		mockDomain Contact
		mockDomain Fmessage
		registerMetaClass Fmessage
		registerMetaClass Activity
		registerMetaClass Trash
		registerMetaClass Contact
		Contact.metaClass.static.withNewSession = {closure -> closure.call()}
		mockParams.messageText = "text"
		mockParams.max = 10
		mockParams.offset = 0
		mockParams.starred = false

		def sahara = new Group(name: "Sahara")
		def thar = new Group(name: "Thar")
		mockDomain Group, [sahara, thar]
		mockDomain GroupMembership, [new GroupMembership(group: sahara, contact: new Contact(mobile: "12345")),
				new GroupMembership(group: sahara, contact: new Contact(mobile: "56484")),
				new GroupMembership(group: thar, contact: new Contact(mobile: "12121")),
				new GroupMembership(group: thar, contact: new Contact(mobile: "22222"))]

		mockMessageSendService = Mock()
		controller.messageSendService = mockMessageSendService
	}

	def "should resend multiple failed message"() {
		setup:
			mockDomain(Fmessage,
					[new Fmessage(id:1L, inbound:false, dispatches:[new Dispatch(dst:"234", status:FAILED)]),
				new Fmessage(id:2L, inbound:false, dispatches:[new Dispatch(dst:"234", status:FAILED)]),
				new Fmessage(id:3L, inbound:false, dispatches:[new Dispatch(dst:"234", status:FAILED)])])
			mockParams.checkedMessageList = (", 1, 2,")
		when:
			controller.retry()
		then:
			1 * mockMessageSendService.retry { it.id == 1L }
			1 * mockMessageSendService.retry { it.id == 2L }
	}

	def "should resend a single failed message"() {
		setup:
			mockDomain(Fmessage,
					[new Fmessage(id:1L, inbound:false, dispatches:[new Dispatch()]),
				new Fmessage(id:2L, inbound:false, dispatches:[new Dispatch()]),
				new Fmessage(id:3L, inbound:false, dispatches:[new Dispatch()])])
			mockParams.messageId = "1"
		when:
			controller.retry()
		then:
			1 * mockMessageSendService.retry { it.id == 1L }
	}
	
	def 'emptyTrash should call trashService_emptyTrash'() {
		given:
			controller.trashService = Mock(TrashService)
		when:
			controller.emptyTrash()
		then:
			1 * controller.trashService.emptyTrash()
	}
	
	def 'move action should work for activities'() {
		given:
			def messageId = 7
			def pollId = 9
			Poll poll = Mock()
			Fmessage message = Mock()
			Activity.metaClass.static.get = { id -> id == pollId? poll: null }
			Fmessage.metaClass.static.get = { id -> id == messageId? message: null }
			Trash.metaClass.static.findByLinkId = { id -> }
		when:
			mockParams.messageId = ',' + messageId + ','
			mockParams.ownerId = pollId
			mockParams.messageSection = 'activity'
			controller.move()
		then:
			1 * poll.addToMessages(message)
	}
}
