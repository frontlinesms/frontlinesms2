package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class MessageControllerSpec extends grails.plugin.spock.IntegrationSpec {
	def controller

	def setup() {
		controller = new MessageController()
	}

	def "Inbound messages show up in inbox view"() {
		setup:
			def messageIn1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, dateRecieved:createDate("2011/01/21")).save(failOnError: true)
			def messageIn2 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, dateRecieved:createDate("2011/01/20")).save(failOnError: true)
			new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:false).save(failOnError: true)
			new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:false).save(failOnError: true)
		when:
			controller.params.messageSection = 'inbox'
			def model = controller.inbox()
		then:
			model.messageInstanceTotal == 2
			model.messageInstanceList == [messageIn1, messageIn2]
	}

	def 'Messages are sorted by date' () {
		setup:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, dateRecieved:createDate("2011/01/20")).save(failOnError: true)
			def message2 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, dateRecieved:createDate("2011/01/24")).save(failOnError: true)
			def message3 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, dateRecieved:createDate("2011/01/23")).save(failOnError: true)
			def message4 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, dateRecieved:createDate("2011/01/21")).save(failOnError: true)
		when:
			controller.params.messageSection = 'inbox'
			def model = controller.inbox()
		then:
			model.messageInstanceTotal == 4
			model.messageInstanceList == [message2, message3, message4, message1]
			model.messageInstanceList != [message1, message2, message3, message4]
	}

	def 'calling "show" action in inbox leads to unread message becoming read'() {
		setup:
			def id = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true).save(failOnError: true).id
			assert Fmessage.get(id).read == false
		when:
			controller.params.id = id
			controller.params.messageSection = 'inbox'
			controller.inbox()
		then:
			Fmessage.get(id).read == true
	}

	def 'calling "show" action leads to read message staying read'() {
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
             def message1 = new Fmessage(inbound:true).save(failOnError: true)
          when:
             controller.inbox()
          then:
             controller.params.messageId == message1.id
        }


	Date createDate(String dateAsString) {
		DateFormat format = createDateFormat();
		return format.parse(dateAsString)
	}

	DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy/MM/dd")
	}
}
