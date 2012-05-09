package frontlinesms2.controller

import spock.lang.*
import grails.plugin.spock.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import frontlinesms2.*

class MessageControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller

	def setup() {
		controller = new MessageController()
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
			 flashMessage.contains("Message has been queued to send to")
			 addresses.each {
				 flashMessage.contains(it)
			 }
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
			 flashMessage.contains("Message has been queued to send to")
			 addresses.each {
				 flashMessage.contains(it)
			 }
	}

	def 'Messages are sorted by date' () {
		setup:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, date:createDate("2011/01/20")).save(failOnError: true)
			def message2 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, date:createDate("2011/01/24")).save(failOnError: true)
			def message3 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, date:createDate("2011/01/23")).save(failOnError: true)
			def message4 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, date:createDate("2011/01/21")).save(failOnError: true)
		when:
			def messageInstanceList = Fmessage.inbox(false, false)
		then:
			messageInstanceList.count() == 4
			messageInstanceList.list(sort:'date', order: 'desc') == [message2, message3, message4, message1]
			messageInstanceList.list(sort:'date', order: 'desc') != [message1, message2, message3, message4]
	}

	def 'calling SHOW action in inbox leads to unread message becoming read'() {
		setup:
			def id = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, date: new Date()).save(failOnError: true).id
			assert Fmessage.get(id).read == false
		when:
			controller.params.messageId = id
			controller.params.messageSection = 'inbox'
			controller.inbox()
		then:
			Fmessage.get(id).read == true
	}

	def 'calling SHOW action leads to read message staying read'() {
		setup:
			def id = new Fmessage(src: '1234567', read:true, date: new Date(), inbound:true).save(failOnError: true).id
			assert Fmessage.get(id).read
		when:
			controller.params.messageSection = 'inbox'
			controller.inbox()
		then:
			Fmessage.get(id).read
	}
	
	def 'calling "starMessage" action leads to unstarred message becoming starred'() {
		setup:
			def id = new Fmessage(src:'Bob', text:'I like manchester', inbound:true, date: new Date()).save(failOnError: true).id
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
			def id = new Fmessage(src:'1234567', read:true, starred:true, date: new Date(), inbound: true).save(failOnError: true).id
			assert Fmessage.get(id).read
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
			controller.response.contentAsString == "Characters remaining 105 (3 SMS messages)"
	}
	
	def 'calling "sendMessageCount" returns the number of characters remaining'() {
		when:
			controller.params.message = "abc123"
			controller.sendMessageCount()
		then:
			controller.response.contentAsString == "Characters remaining 154 (1 SMS message)"
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
			poll.refresh().messages == [message]
	}
	
	Date createDate(String dateAsString) {
		DateFormat format = createDateFormat();
		return format.parse(dateAsString)
	}

	DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy/MM/dd")
	}
}
