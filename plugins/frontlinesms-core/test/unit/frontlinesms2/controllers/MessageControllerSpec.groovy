package frontlinesms2.controllers

import static frontlinesms2.DispatchStatus.*
import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*
import grails.buildtestdata.mixin.Build

@TestFor(MessageController)
@Mock([Contact, TextMessage, Group, GroupMembership, Poll, Trash])
@Build([Poll, TextMessage])
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

		TextMessage.metaClass.static.getAll = { List ids ->
			ids.collect { TextMessage.get(it) } }

		mockMessageSendService = Mock()
		controller.messageSendService = mockMessageSendService
	}

	def "should resend a single failed message"() {
		setup:
			[new TextMessage(text:'', id:1, inbound:false, dispatches:[new Dispatch()]),
					new TextMessage(text:'', id:2, inbound:false, dispatches:[new Dispatch()]),
					new TextMessage(text:'', id:3, inbound:false, dispatches:[new Dispatch()])]*.save(failOnError:true)*.id
			params.interactionId = 1
			1 * mockMessageSendService.retry(_) >> { m ->
				assert m*.id == [1]
				return 1 }
		when:
			controller.retry()
		then:
			true
	}

	def "should resend multiple failed message"() {
		setup:
			[new TextMessage(text:'', id:1L, inbound:false, dispatches:[new Dispatch(dst:"234", status:FAILED)]),
				new TextMessage(text:'', id:2L, inbound:false, dispatches:[new Dispatch(dst:"234", status:FAILED)]),
				new TextMessage(text:'', id:3L, inbound:false, dispatches:[new Dispatch(dst:"234", status:FAILED)])]*.save(failOnError:true)
			params['interaction-select'] = [1, 2]
			2 * mockMessageSendService.retry(_) >> { m ->
				return 1 }
		when:
			controller.retry()
		then:
			true
	}
	
	def 'emptyTrash should call trashService_emptyTrash'() {
		given:
			controller.trashService = Mock(TrashService)
		when:
			controller.emptyTrash()
		then:
			1 * controller.trashService.emptyTrash()
	}

	def "archiving a message should redirect to the calling action without a interactionId"() {
		given:
			params.controller = "message"
			params.messageSection = "inbox"
			params.interactionId = TextMessage.build().id
		when:
			controller.archive()
		then:
			controller.response.redirectUrl == "/message/inbox?ownerId=&starred=false&failed=&searchId="
	}

	def "archiving a message IN SEARCH should redirect to the calling action without a interactionId"() {
		given:
			params.controller = "message"
			params.messageSection = "result"
			params.interactionId = TextMessage.build().id
			params.searchId = 1
		when:
			controller.archive()
		then:
			controller.response.redirectUrl == "/search/result?searchId=1"
	}
	
	def "archiving pending messages from the result screen should fail"(){
		setup:
			def message = new TextMessage(text:'', id:2L, inbound:false, dispatches:[new Dispatch(dst:"234", status:PENDING)]).save()
			params.controller = "message"
			params.messageSection = "result"
			params.searchId = "1"
			params.interactionId = message.id
		when:
			controller.archive()
		then:
			assert message.archived == false
	}
}

