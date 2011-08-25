package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class PendingMessageSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		new Fmessage(src: "src1", dst:"dst1", status: MessageStatus.SEND_FAILED, starred: true).save(flush: true)
		new Fmessage(src: "src2", dst:"dst2", status: MessageStatus.SEND_PENDING).save(flush: true)
		new Fmessage(src: "src", dst:"dst1", status: MessageStatus.SENT).save(flush: true)
		new Fmessage(src: "src", status: MessageStatus.INBOUND).save(flush: true)
	}

	def 'should list all the pending messages'() {
		when:
			to MessagesPage
			$('a', text: "Pending").click()
			waitFor { title == "Pending" }
			def messages = $('#messages tbody tr')
		then:
			messages.size() == 2
		    messages.collect { it.find("td:nth-child(3) a").text()}.containsAll(["dst1", "dst2"])
	}
	
	def "'Reply All' button does not appears for multiple selected messages"() {
		when:
			to MessagesPage
			$('a', text:"Pending").click()
			waitFor { title == "Pending" }
			
			$("#message")[1].click()
			$("#message")[2].click()
			sleep 1000
		then:
			!$('.multi-action a', text:'Reply All').displayed
	}

	def "should filter pending messages for starred and unstarred messages"() {
		when:
			to MessagesPage
			$('a', text: "Pending").click()
			waitFor { title == "Pending" }
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
	
	
}
