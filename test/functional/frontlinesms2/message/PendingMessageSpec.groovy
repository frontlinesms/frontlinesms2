package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class PendingMessageSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		new Fmessage(src: "src1", status: MessageStatus.SEND_FAILED, starred: true).save(flush: true)
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
		    messages.collect { it.find("td:nth-child(3) a").text()}.containsAll(["src1", "src2"])
	}


	def "reply option should not be available for messages listed in poll section"() {
		when:
			to MessagesPage
			$('#messages-menu li a', href:'/frontlinesms2/message/pending').click()
			waitFor { title == "Pending" }
		then:
		    !$('a', text:'Reply')
	}

	def "should filter pending messages for starred and unstarred messages"() {
		when:
			to MessagesPage
			$('#messages-menu li a', href:'/frontlinesms2/message/pending').click()
			waitFor { title == "Pending" }
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
			waitFor {$("#messages tbody tr").size() == 1}
		then:
			$("#messages tbody tr")[0].find("td:nth-child(3)").text() == 'src1'
		when:
			$('a', text:'All').click()
			waitFor {$("#messages tbody tr").size() == 2}
		then:
			$("#messages tbody tr").collect {it.find("td:nth-child(3)").text()}.containsAll(['src1', 'src2'])
	}
}
