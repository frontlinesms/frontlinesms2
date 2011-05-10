package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class MessageControllerSpec extends ControllerSpec {
	def 'inbox closure requests correct messages'() {
		setup:
			mockDomain(Fmessage)
		when:
			controller.inbox()
		then:
			mockParams.inbound
	}

	def "sent closure requests correct messages"() {
		setup:
			mockDomain(Fmessage)
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
			Date date1 = createDate("2011/01/20")
			Date date2 = createDate("2011/01/24")
			Date date3 = createDate("2011/01/23")
			Date date4 = createDate("2011/01/21")

			def message1 = new Fmessage(inbound:true, dateCreated:date1)
			def message2 = new Fmessage(inbound:true, dateCreated:date2)
			def message3 = new Fmessage(inbound:true, dateCreated:date3)
			def message4 = new Fmessage(inbound:true, dateCreated:date4)
			mockDomain(Fmessage, [message1, message2, message3, message4])
		when:
			def model = controller.inbox()
		then:
			model.messageInstanceTotal == 4
			model.messageInstanceList == [message1, message4, message3, message2]
			model.messageInstanceList != [message1, message2, message3, message4]
	}

	Date createDate(String dateAsString) {
		DateFormat format = createDateFormat();
		return format.parse(dateAsString)
	}

	DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy/MM/dd")
	}
}

