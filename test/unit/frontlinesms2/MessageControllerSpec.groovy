package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class MessageControllerSpec extends ControllerSpec {
	def setup() {
		mockDomain(Fmessage)
		mockDomain(Poll)
	}

	def 'inbox closure requests correct messages'() {
		when:
			controller.inbox()
		then:
			mockParams.inbound
	}

	def "sent closure requests correct messages"() {
		when:
			controller.sent()
		then:
			!mockParams.inbound
	}

	def "Inbound messages show up in inbox view"() {
		setup:
			def messageIn1 = new Fmessage(inbound:true)
			def messageIn2 = new Fmessage(inbound:true)
			def messageOut1 = new Fmessage(inbound:false)
			def messageOut2 = new Fmessage(inbound:false)
			mockDomain(Fmessage, [messageIn1, messageIn2, messageOut1, messageOut2])
		when:
			def model = controller.inbox()
		then:
			model.messageInstanceTotal == 2
			model.messageInstanceList == [messageIn1, messageIn2]
	}

	def "Outbound messages show up in sent view"() {
		setup:
			def messageIn1 = new Fmessage(inbound:true)
			def messageIn2 = new Fmessage(inbound:true)
			def messageOut1 = new Fmessage(inbound:false)
			def messageOut2 = new Fmessage(inbound:false)
			mockDomain(Fmessage, [messageIn1, messageIn2, messageOut1, messageOut2])
		when:
			def model = controller.sent()
		then:
			model.messageInstanceTotal == 2
			model.messageInstanceList == [messageOut1, messageOut2]
	}

	def 'Messages are sorted by date' () {
		setup:
			def message1 = new Fmessage(inbound:true, dateCreated:createDate("2011/01/20"))
			def message2 = new Fmessage(inbound:true, dateCreated:createDate("2011/01/24"))
			def message3 = new Fmessage(inbound:true, dateCreated:createDate("2011/01/23"))
			def message4 = new Fmessage(inbound:true, dateCreated:createDate("2011/01/21"))
			mockDomain(Fmessage, [message1, message2, message3, message4])
		when:
			def model = controller.inbox()
		then:
			model.messageInstanceTotal == 4
			model.messageInstanceList == [message2, message3, message4, message1]
			model.messageInstanceList != [message1, message2, message3, message4]
		when:
			mockParams.max = 2
			mockParams.offset = 2
			model = controller.inbox()
		then:
			model.messageInstanceTotal == 4
			model.messageInstanceList == [message4, message1]
	}
	
	def 'calling "show" action in inbox leads to unread message becoming read'() {
		setup:
			mockDomain(Contact)
			def id = new Fmessage().save(failOnError: true).id
			assert Fmessage.get(id).read == false
		when:
			mockParams.messageSection = 'inbox'
			mockParams.id = id
			controller.show()
		then:
			Fmessage.get(id).read
	}
	
	def 'calling "show" action leads to read message staying read'() {
		setup:
			mockDomain(Contact)
			def id = new Fmessage(read:true).save(failOnError: true).id
			assert Fmessage.get(id).read
		when:
			mockParams.id = id
			controller.inbox()
		then:
			Fmessage.get(id).read
	}

	Date createDate(String dateAsString) {
		DateFormat format = createDateFormat();
		return format.parse(dateAsString)
	}

	DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy/MM/dd")
	}
}

