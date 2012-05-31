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
		controller.beforeInterceptor.call()

		controller.params.messageText = "text"
		controller.params.max = 10
		controller.params.offset = 0
		controller.params.starred = false
		
		def sahara = new Group(name: "Sahara").save(flush: true)
		def thar = new Group(name: "Thar").save(flush: true)

		controller.metaClass.getPaginationCount = {-> return 10}
	}

	def "should send a message to the given address"() {
		setup:
			controller.params.addresses = "+919544426000"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			controller.flash.message == "Message has been queued to send to +919544426000"
	}

	def "should send message to each recipient in the list of address"() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			controller.params.addresses = addresses
		when:
			assert Fmessage.count() == 0
			controller.send()
			def flashMessage = controller.flash.message 
		then:
			assert Fmessage.count() == 1
			(Fmessage.getAll() as List)[0].dispatches*.dst.sort() == addresses.sort()
	}

	def "should display flash message on successful message sending"() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			controller.params.addresses = addresses
		when:
			assert Fmessage.count() == 0
			controller.send()
			def flashMessage = controller.flash.message 
		then:
			 flashMessage.contains("Message has been queued to send to 3 recipients")
	}

	def 'Messages are sorted by date' () {
		setup:
			def message1 = Fmessage.build(date:createDate("2011/01/20"))
			def message2 = Fmessage.build(date:createDate("2011/01/24"))
			def message3 = Fmessage.build(date:createDate("2011/01/23"))
			def message4 = Fmessage.build(date:createDate("2011/01/21"))
		when:
			def messageInstanceList = Fmessage.inbox(false, false)
		then:
			messageInstanceList.count() == 4
			messageInstanceList.list(sort:'date', order: 'desc') == [message2, message3, message4, message1]
			messageInstanceList.list(sort:'date', order: 'desc') != [message1, message2, message3, message4]
	}

	def 'calling SHOW action in inbox leads to unread message becoming read'() {
		setup:
			def id = Fmessage.build().id
			assert Fmessage.get(id).read == false
		when:
			controller.params.messageId = id
			controller.params.messageSection = 'inbox'
			controller.show()
		then:
			Fmessage.get(id).read == true
	}

	def 'calling SHOW action leads to read message staying read'() {
		setup:
			def id = Fmessage.build(read:true).id
			assert Fmessage.get(id).read
		when:
			controller.params.messageSection = 'inbox'
			controller.inbox()
		then:
			Fmessage.get(id).read
	}
	
	def 'calling "starMessage" action leads to unstarred message becoming starred'() {
		setup:
			def id = Fmessage.build().id
			assert Fmessage.get(id).read == false
		when:
			controller.params.messageId = id
			controller.params.messageSection = 'inbox'
			controller.changeStarStatus()
		then:
			Fmessage.get(id).starred == true
	}

	def 'calling "starMessage" action leads to starred message becoming unstarred'() {
		setup:
			def id = Fmessage.build(starred:true).id
			assert Fmessage.get(id).starred
		when:
			controller.params.messageId = id
			controller.params.messageSection = 'inbox'
			controller.changeStarStatus()
		then:
			Fmessage.get(id).starred == false
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
			def poll = new Poll(name:'whatever')
					.addToResponses(PollResponse.createUnknown())
					.addToResponses(key:'A', value:'a')
					.addToResponses(key:'B', value:'b')
					.save(failOnError:true, flush:true)
			def message = Fmessage.build()
		when:
			controller.params.messageId = message.id
			controller.params.ownerId = poll.id
			controller.params.messageSection = 'activity'
			controller.move()
		then:
			message.messageOwner == poll
			// FIXME this next should work but doesn't. 
			//poll.messages == [message]
	}

	def 'listRecipients should return a JSON list of contact displayNames and message statuses'() {
		given:
			new Contact(mobile:'456', name:'bertrand').save()
			new Contact(mobile:'123', name:'andrea').save()
			def m = new Fmessage()
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
	
	private Date createDate(String dateAsString) {
		new SimpleDateFormat("yyyy/MM/dd").parse(dateAsString)
	}
}

