package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*

class PendingMessageSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		new Fmessage(src: "src1", dst:"dst1", status: MessageStatus.SEND_FAILED, starred: true).save(flush: true)
		new Fmessage(src: "src2", dst:"dst2", status: MessageStatus.SEND_PENDING).save(flush: true)
		new Fmessage(src: "src", dst:"dst1", status: MessageStatus.SENT).save(flush: true)
		new Fmessage(src: "src", status: MessageStatus.INBOUND).save(flush: true)
	}

	def 'should list all the pending messages'() {
		when:
			goToPendingPage()
			def messages = $('#messages tbody tr')
		then:
			messages.size() == 2
			messages.collect { it.find("td:nth-child(3) a").text() }.containsAll(["dst1", "dst2"])
	}
	
	def "'Reply All' button does not appears for multiple selected messages"() {
		when:
			goToPendingPage()
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { !$('.multi-action a', text:'Reply All').displayed }
	}

	def "should filter pending messages for pending and failed messages"() {
		when:
			goToPendingPage()
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Failed').click()
		then:	
			waitFor { $("#messages tbody tr").size() == 1 }
			$("#messages tbody tr")[0].find("td:nth-child(3)").text() == 'dst1'
		when:
			$('a', text:'All').click()
		then:	
			waitFor { $("#messages tbody tr").size() == 2 }
			$("#messages tbody tr").collect {it.find("td:nth-child(3)").text()}.containsAll(['dst1', 'dst2'])
	}

	def "retry button must not apper if there are no failed messages"() {
		setup:
			Fmessage.list()*.delete(flush: true)
			new Fmessage(src: "src", "dst": "dst", status: MessageStatus.SEND_PENDING).save(flush: true)
			new Fmessage(src: "src", "dst": "dst", status: MessageStatus.SEND_PENDING).save(flush: true)
			assert Fmessage.count() == 2
		when:
			goToPendingPage()
		then:
			!$("#retry").displayed
		when:
			messagesSelect[0].click()
		then:
			waitFor { $("#multiple-messages").displayed }
			!$("#retry-failed").displayed
	}

	def "should be able to retry a failed message"() {
		when:
			goToPendingPage()
			$("a", text:"dst1").click()
		then:
			waitFor { $("#retry").displayed }
		when:
			$("#retry").click()
		then:	
			waitFor{ $(".flash").text().contains("dst1") }
	}

	def "should be able to retry all failed messages"() {
		setup:
			new Fmessage(src: "src1", dst:"dst2", status: MessageStatus.SEND_FAILED, starred: true).save(flush: true, failOnError:true)
		when:
			goToPendingPage()
		then:
			$("#retry").displayed
		when:
			messagesSelect[0].click()
		then:
			waitFor { $("#retry-failed").displayed }
		when:
			$("#retry-failed").click()
		then:
			waitFor{ $(".flash").text().contains("dst2, dst1") }
	}

	def goToPendingPage() {
		to MessagesPage
		$('a', text: "Pending").click()
		waitFor { title == "Pending" }
	}
}
