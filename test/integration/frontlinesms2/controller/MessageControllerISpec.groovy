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
		then:
			controller.flash.message == "Message has been queued to send to +919544426000, +919004030030, +1312456344"
	}

	def "should display flash message on successful message sending"() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			controller.params.addresses = addresses
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			controller.flash.message == "Message has been queued to send to +919544426000, +919004030030, +1312456344"
	}

	def 'Messages are sorted by date' () {
		setup:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND, dateReceived:createDate("2011/01/20")).save(failOnError: true)
			def message2 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND, dateReceived:createDate("2011/01/24")).save(failOnError: true)
			def message3 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND, dateReceived:createDate("2011/01/23")).save(failOnError: true)
			def message4 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND, dateReceived:createDate("2011/01/21")).save(failOnError: true)
		when:
			def messageInstanceList = Fmessage.inbox(false, false)
		then:
			messageInstanceList.count() == 4
			messageInstanceList.list(sort:'dateReceived', order: 'desc') == [message2, message3, message4, message1]
			messageInstanceList.list(sort:'dateReceived', order: 'desc') != [message1, message2, message3, message4]
	}

	def 'calling SHOW action in inbox leads to unread message becoming read'() {
		setup:
			def id = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save(failOnError: true).id
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
			def id = new Fmessage(read:true).save(failOnError: true).id
			assert Fmessage.get(id).read
		when:
			controller.params.messageSection = 'inbox'
			controller.inbox()
		then:
			Fmessage.get(id).read
	}
	
	def 'calling "starMessage" action leads to unstarred message becoming starred'() {
		setup:
			def id = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true).save(failOnError: true).id
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
			def id = new Fmessage(read:true, starred:true).save(failOnError: true).id
			assert Fmessage.get(id).read
		when:
			controller.params.messageId = id
			controller.params.messageSection = 'inbox'
			controller.changeStarStatus()
		then:
			Fmessage.get(id).starred == false
	}

	def 'empty trash permanently deletes messages with deleted flag true'() {
		setup:
			(1..3).each {new Fmessage(deleted:false).save()}
			def inboxMessages = Fmessage.list()
			(1..3).each {new Fmessage(deleted:true).save()}
		when:
			controller.emptyTrash()
		then:
			Fmessage.list() == inboxMessages
	}
	
	Date createDate(String dateAsString) {
		DateFormat format = createDateFormat();
		return format.parse(dateAsString)
	}

	DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy/MM/dd")
	}
}
