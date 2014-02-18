package frontlinesms2.controller

import spock.lang.*
import grails.plugin.spock.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import frontlinesms2.*
import grails.converters.JSON

class MessageControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def trashService

	def setup() {
		controller = new MessageController()
		controller.trashService = trashService
		controller.beforeInterceptor.action.call()

		controller.params.messageText = "text"
		controller.params.max = 10
		controller.params.offset = 0
		controller.params.starred = false

		new Group(name: "Sahara").save(flush: true)
		new Group(name: "Thar").save(flush: true)

		controller.metaClass.getPaginationCount = { -> return 10 }
	}

	def "should send a message to the given address"() {
		setup:
			controller.params.addresses = "+919544426000"
		when:
			controller.send()
		then:
			TextMessage.count() == 1
			(TextMessage.getAll() as List)[0].dispatches*.dst == ['+919544426000']
	}

	def "should send message to each recipient in the list of address"() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			controller.params.addresses = addresses
			assert TextMessage.count() == 0
		when:
			controller.send()
		then:
			TextMessage.count() == 1
			(TextMessage.getAll() as List)[0].dispatches*.dst.sort() == addresses.sort()
	}

	def 'should render message on successful message sending'() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			controller.params.addresses = addresses
		when:
			controller.send()
		then:
			controller.response.contentAsString == 'fmessage.queued.multiple[3]'
	}

	def 'Messages are sorted by date' () {
		setup:
			def message1 = TextMessage.build(date:createDate("2011/01/20"))
			def message2 = TextMessage.build(date:createDate("2011/01/24"))
			def message3 = TextMessage.build(date:createDate("2011/01/23"))
			def message4 = TextMessage.build(date:createDate("2011/01/21"))
		when:
			def interactionInstanceList = TextMessage.inbox(false, false)
		then:
			interactionInstanceList.count() == 4
			interactionInstanceList.list(sort:'date', order: 'desc') == [message2, message3, message4, message1]
			interactionInstanceList.list(sort:'date', order: 'desc') != [message1, message2, message3, message4]
	}

	def 'calling SHOW action in inbox leads to unread message becoming read'() {
		setup:
			def id = TextMessage.build().id
			assert TextMessage.get(id).read == false
		when:
			controller.params.messageId = id
			controller.params.messageSection = 'inbox'
			controller.show()
		then:
			TextMessage.get(id).read == true
	}

	def 'calling SHOW action leads to read message staying read'() {
		setup:
			def id = TextMessage.build(read:true).id
			assert TextMessage.get(id).read
		when:
			controller.params.messageSection = 'inbox'
			controller.inbox()
		then:
			TextMessage.get(id).read
	}

	def 'calling "starMessage" action leads to unstarred message becoming starred'() {
		setup:
			def id = TextMessage.build().id
			assert TextMessage.get(id).read == false
		when:
			controller.params.messageId = id
			controller.params.messageSection = 'inbox'
			controller.changeStarStatus()
		then:
			TextMessage.get(id).starred == true
	}

	def 'calling "starMessage" action leads to starred message becoming unstarred'() {
		setup:
			def id = TextMessage.build(starred:true).id
			assert TextMessage.get(id).starred
		when:
			controller.params.messageId = id
			controller.params.messageSection = 'inbox'
			controller.changeStarStatus()
		then:
			TextMessage.get(id).starred == false
	}

	def 'calling "sendMessageCount" returns the number of messages to be sent'() {
		when:
			controller.params.message = "!@:%^&*(){" * 30
			controller.sendMessageCount()
		then:
			controller.response.contentAsString == '{"charCount":300,"partCount":3,"remaining":105}'
	}

	def 'calling "sendMessageCount" returns the number of characters remaining'() {
		when:
			controller.params.message = "abc123"
			controller.sendMessageCount()
		then:
			controller.response.contentAsString == '{"charCount":6,"partCount":1,"remaining":154}'
	}

	def 'move action should work for activities'() {
		given:
			def poll = TestData.createFootballPoll()
			def message = TextMessage.build()
		when:
			controller.params.messageId = message.id
			controller.params.ownerId = poll.id
			controller.params.messageSection = 'activity'
			controller.move()
		then:
			message.messageOwner == poll
			poll.messages*.id.contains(message.id)
	}

	def 'listRecipients should return a JSON list of contact displayNames and message statuses'() {
		given:
			new Contact(mobile:'456', name:'bertrand').save()
			new Contact(mobile:'123', name:'andrea').save()
			def m = new TextMessage(text:"")
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
					.addToDispatches(dst:'456', status:DispatchStatus.SENT, dateSent:new Date())
					.addToDispatches(dst:'789', status:DispatchStatus.FAILED)
					.save(failOnError:true)
			controller.params.messageId = m.id
		when:
			controller.listRecipients()
		then:
			JSON.parse(controller.response.contentAsString) == [[display:'789', status:'FAILED'], [display:'andrea', status:'PENDING'], [display:'bertrand', status:'SENT']]
	}

	def "Message should not remain in old PollResponse after moving it to another activity"() {
		given:
			def m = TextMessage.build(inbound:true)
			def responseA = new PollResponse(key:'A', value:'TessstA')
			def previousOwner = new Poll(name:'This is a poll', question:'What is your name?')
					.addToResponses(responseA)
					.addToResponses(new PollResponse(key:'B' , value:'TessstB'))
					.addToResponses(PollResponse.createUnknown())
					.addToMessages(m)
			previousOwner.save(failOnError:true)
			responseA.addToMessages(m)
			previousOwner.save(failOnError:true)

			assert responseA.messages.contains(m)

			def Keyword k = new Keyword(value:'ASDF')
			def newOwner = new Autoreply(name:"Toothpaste", autoreplyText: "Thanks for the input").addToKeywords(k).save(failOnError:true)

			// TODO move this test to MessageController
			controller.params.messageId = m.id
			controller.params.ownerId = newOwner.id
			controller.params.messageSection = 'activity'
		when:
			controller.move()
		then:
			!responseA.messages.contains(m)
	}

	def 'archived messages should not be allowed to move'(){
		setup:
			def announcement = Announcement.build(name:"Archived activity",sentMessageText:"text message",archived:true)
			def announcement2 = Announcement.build(name:"Moving Into Announcement",sentMessageText:"text")
			def m1 = TextMessage.build(archived:true)//archived message fails
			def m2 = TextMessage.build(messageOwner:announcement,archived:true)//archived owner fails
			def m3 = TextMessage.build()//inbox message passes
			def controller = new MessageController()
			controller.params['message-select'] = []
			controller.params['message-select'] << m1.id << m2.id << m3.id
			controller.params.ownerId = announcement2.id
		when:
			controller.move()
		then:
			m1.messageOwner != announcement2
			m2.messageOwner != announcement2
			m3.messageOwner == announcement2
	}

	def 'move action should reset message.ownerDetail'() {
		given:
			def webConnection =  GenericWebconnection.build()
			def announcement = Announcement.build()
			def message = TextMessage.build()
			message.setMessageDetail(webConnection, "ownerdetail-pending")
			webConnection.addToMessages(message)
			webConnection.save(failOnError:true, flush:true)
		when:
			controller.params.messageId = message.id
			controller.params.ownerId = announcement.id
			controller.params.messageSection = 'activity'
			controller.move()
		then:
			message.messageOwner.id == announcement.id
			message.ownerDetail != 'ownerdetail-pending'
	}

	def 'moving messages to different activities and sections should work'() {
		given:
			def message = TextMessage.build(inbound:true, text:'the message')
			def autoforward = Autoforward.build(name:'autoforward')
			def autoreply = Autoreply.build(name:'autoreply')
			def webconnection = GenericWebconnection.build(name:'webconnection')
			def subscription = Subscription.build(name:'subscription')
			def poll = new Poll(name: 'This is a poll')
			poll.editResponses(choiceA: 'Manchester', choiceB:'Barcelona')
			poll.save(failOnError:true, flush:true)

			controller.params.messageId = message.id
			controller.params.ownerId = poll.id
			controller.params.messageSection = 'activity'
		when:
			controller.move()
		then:
			message.messageOwner.id == poll.id
		when:
			controller.params.ownerId = autoforward.id
			controller.move()
		then:
			message.messageOwner.id == autoforward.id
		when:
			controller.params.ownerId = autoreply.id
			controller.move()
		then:
			message.messageOwner.id == autoreply.id
		when:
			controller.params.ownerId = webconnection.id
			controller.move()
		then:
			message.messageOwner.id == webconnection.id
		when:
			controller.params.ownerId = subscription.id
			controller.move()
		then:
			message.messageOwner.id == subscription.id
		}

	private Date createDate(String dateAsString) {
		new SimpleDateFormat("yyyy/MM/dd").parse(dateAsString)
	}
}
