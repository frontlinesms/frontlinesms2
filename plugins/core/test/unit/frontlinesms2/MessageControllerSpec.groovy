package frontlinesms2

import spock.lang.*
import grails.test.mixin.*
import grails.buildtestdata.mixin.Build

import static frontlinesms2.DispatchStatus.*

@TestFor(MessageController)
@Mock([Contact, Fmessage, Group, GroupMembership, Poll, Trash])
@Build([Poll, Fmessage])
class MessageControllerSpec extends Specification {
	MessageSendService mockMessageSendService

	def setup() {
		controller.metaClass.message = { Map args -> args.code }
		controller.metaClass.getPaginationCount = { -> 10 }
		Contact.metaClass.static.withNewSession = {closure -> closure.call()}
		params.messageText = "text"
		params.max = 10
		params.offset = 0
		params.starred = false

		def sahara = new Group(name: "Sahara")
		def thar = new Group(name: "Thar")
		[sahara, thar]*.save()
		[new GroupMembership(group: sahara, contact: new Contact(mobile: "12345")),
				new GroupMembership(group: sahara, contact: new Contact(mobile: "56484")),
				new GroupMembership(group: thar, contact: new Contact(mobile: "12121")),
				new GroupMembership(group: thar, contact: new Contact(mobile: "22222"))]*.save()

		Fmessage.metaClass.static.getAll = { List ids ->
			Fmessage.findAll().findAll { it.id in ids } }

		mockMessageSendService = Mock()
		controller.messageSendService = mockMessageSendService
	}

	def "should resend multiple failed message"() {
		setup:
			[new Fmessage(id:1L, inbound:false, dispatches:[new Dispatch(dst:"234", status:FAILED)]),
				new Fmessage(id:2L, inbound:false, dispatches:[new Dispatch(dst:"234", status:FAILED)]),
				new Fmessage(id:3L, inbound:false, dispatches:[new Dispatch(dst:"234", status:FAILED)])]*.save()
			params['message-select'] = [1, 2]
		when:
			controller.retry()
		then:
			1 * mockMessageSendService.retry { it.id == 1L }
			1 * mockMessageSendService.retry { it.id == 2L }
	}

	def "should resend a single failed message"() {
		setup:
			[new Fmessage(id:1L, inbound:false, dispatches:[new Dispatch()]),
				new Fmessage(id:2L, inbound:false, dispatches:[new Dispatch()]),
				new Fmessage(id:3L, inbound:false, dispatches:[new Dispatch()])]*.save()
			params.messageId = "1"
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

	def "archiving a message should redirect to the calling action without a messageId"() {
		given:
			params.controller = "message"
			params.messageSection = "inbox"
			params.messageId = "1"
		when:
			controller.archive()
		then:
			controller.response.redirectUrl == "/message/inbox?ownerId=&starred=false&failed=&searchId=&flashMessage=default.archived.message"
	}

	def "archiving a message IN SEARCH should redirect to the calling action without a messageId"() {
		given:
			params.controller = "message"
			params.messageSection = "result"
			params.searchId = "1"
		when:
			controller.archive()
		then:
			controller.response.redirectUrl == "/search/result?searchId=1&flashMessage=default.archived.message"
	}
}
