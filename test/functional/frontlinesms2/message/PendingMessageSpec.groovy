package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class PendingMessageSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		new Fmessage(src: "src1", status: MessageStatus.SEND_FAILED).save(flush: true)
		new Fmessage(src: "src2", status: MessageStatus.SEND_PENDING).save(flush: true)
		new Fmessage(src: "src", status: MessageStatus.SENT).save(flush: true)
		new Fmessage(src: "src", status: MessageStatus.INBOUND).save(flush: true)
	}

	def cleanup() {
		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}

	def 'should list all the pending messages'() {
		when:
			to MessagesPage
			$('#messages-menu li a', href:'/frontlinesms2/message/pending').click()
			waitFor { title == "Pending" }
			def messages = $('#messages tbody tr')
		then:
			messages.size() == 2
			messages*.getAttribute('class').each {it.contains("SEND_FAILED") || it.contains("SEND_PENDING")}
		    messages.collect { it.find("td:nth-child(2) a").text()}.containsAll(["src1", "src2"])
	}
}
