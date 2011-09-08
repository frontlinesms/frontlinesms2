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
		    messages.collect { it.find("td:nth-child(3) a").text()}.containsAll(["dst1", "dst2"])
	}
	
	def "'Reply All' button does not appears for multiple selected messages"() {
		when:
		    goToPendingPage()
			$("#message")[1].click()
			$("#message")[2].click()
			sleep 1000
		then:
			!$('.multi-action a', text:'Reply All').displayed
	}

	def "should filter pending messages for starred and unstarred messages"() {
		when:
			goToPendingPage()
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
			waitFor {$("#messages tbody tr").size() == 1}
		then:
			$("#messages tbody tr")[0].find("td:nth-child(3)").text() == 'dst1'
		when:
			$('a', text:'All').click()
			waitFor {$("#messages tbody tr").size() == 2}
		then:
			$("#messages tbody tr").collect {it.find("td:nth-child(3)").text()}.containsAll(['dst1', 'dst2'])
	}

    def "retry button must not apper if there are no failed messages"() {
		setup:
			Fmessage.list()*.delete(flush: true)
			new Fmessage(src: "src", "dst": "dst", status: MessageStatus.SEND_PENDING).save(flush: true)
			new Fmessage(src: "src", "dst": "dst", status: MessageStatus.SEND_PENDING).save(flush: true)
		when:
			assert Fmessage.count() == 2
			goToPendingPage()
		then:
			!$("#retry").displayed
		when:
			$("#message")[0].click()
			sleep(4000)
			waitFor{$("#multiple-messages").displayed}
		then:
			!$("#retry-failed").displayed
	}

	def "should be able to retry a failed message"() {
		when:
			goToPendingPage()
			$("a", text:"dst1").click()
			sleep(1000)
		then:
			$("#retry").displayed
		when:
			$("#retry").click()
			sleep(2000)
			waitFor{$(".flash").text().contains("dst1")}
		then:
			$(".flash").text().contains("dst1")
	}

	def "should be able to retry all failed messages"() {
		setup:
			new Fmessage(src: "src1", dst:"dst2", status: MessageStatus.SEND_FAILED, starred: true).save(flush: true)
		when:
			goToPendingPage()
		then:
			$("#retry").displayed
		when:
			$("#message")[0].click()
			sleep(1000)
			waitFor {$("#retry-failed").displayed}
			$("#retry-failed").click()
			sleep(2000)
			waitFor{$(".flash").text().contains("dst1, dst2")}
		then:
			$(".flash").text().contains("dst1, dst2")
	}

	def goToPendingPage() {
		to MessagesPage
		$('a', text: "Pending").click()
		waitFor { title == "Pending" }
	}



}
