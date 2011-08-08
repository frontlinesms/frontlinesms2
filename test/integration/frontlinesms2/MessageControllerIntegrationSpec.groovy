package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import frontlinesms2.enums.MessageStatus

class MessageControllerIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def controller

	def setup() {
		controller = new MessageController()
	}
	
	def cleanup() {
		Fmessage.findAll()*.delete(flush:true, failOnError:true)
	}

	def "Inbound messages show up in inbox view"() {
		setup:
			def messageIn1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND, dateReceived:createDate("2011/01/21")).save(failOnError: true)
			def messageIn2 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND, dateReceived:createDate("2011/01/20")).save(failOnError: true)
			new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester').save(failOnError: true)
			new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester').save(failOnError: true)
		when:
			controller.params.messageSection = 'inbox'
			controller.params.archived = false
			def model = controller.inbox()
		then:
			model.messageInstanceTotal == 2
			model.messageInstanceList == [messageIn1, messageIn2]
	}

	def 'Messages are sorted by date' () {
		setup:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND, dateReceived:createDate("2011/01/20")).save(failOnError: true)
			def message2 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND, dateReceived:createDate("2011/01/24")).save(failOnError: true)
			def message3 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND, dateReceived:createDate("2011/01/23")).save(failOnError: true)
			def message4 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND, dateReceived:createDate("2011/01/21")).save(failOnError: true)
		when:
			controller.params.messageSection = 'inbox'
			controller.params.archived = false
			def model = controller.inbox()
		then:
			model.messageInstanceTotal == 4
			model.messageInstanceList == [message2, message3, message4, message1]
			model.messageInstanceList != [message1, message2, message3, message4]
	}

	def 'calling SHOW action in inbox leads to unread message becoming read'() {
		setup:
			def id = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save(failOnError: true).id
			assert Fmessage.get(id).read == false
		when:
			controller.params.id = id
			controller.params.messageSection = 'inbox'
			controller.params.archived = false
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

	def "first message in the inbox view is selected by default"() {
        setup:
             def message1 = new Fmessage(status:MessageStatus.INBOUND).save(failOnError: true)
        when:
			controller.params.archived = false
            def resultMap =  controller.inbox()
        then:
            resultMap['messageInstance'].id == message1.id
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

	def 'deleted messages are returned by trash method and the first message is displayed'() {
		setup:
			(1..3).each {new Fmessage(deleted:true).save()}
			def deletedMessages = Fmessage.list()
			(1..2).each {new Fmessage(deleted:false).save()}
		when:
			def resultMap = controller.trash()
		then:
			resultMap['messageInstanceList'] == deletedMessages
			resultMap['messageInstance'].id == deletedMessages[0].id
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
