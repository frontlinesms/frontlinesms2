package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*

class MessagePendingSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		new Fmessage(src: "src1", dst:"dst1", hasFailed:true, starred: true).save(flush: true)
		new Fmessage(src: "src2", dst:"dst2", hasPending:true).save(flush: true)
		new Fmessage(src: "src", dst:"dst1", hasSent:true).save(flush: true)
		new Fmessage(src: "src", inbound:true).save(flush: true)
	}

	def 'should list all the pending messages'() {
		when:
			go "message/pending"
		then:
			at PageMessagePending
		when:
			def messages = $('#messages tbody tr')
		then:
			messages.size() == 2
			messages.collect { it.find("td:nth-child(3) a").text() }.containsAll(["dst1", "dst2"])
	}
	
	def "'Reply All' button does not appears for multiple selected messages"() {
		when:
			go "message/pending"
		then:
			at PageMessagePending
		when:
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { !$('.multi-action a', text:'Reply All').displayed }
	}

	def "should filter pending messages for pending and failed messages"() {
		when:
			go "message/pending"
		then:
			at PageMessagePending
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
			new Fmessage(src:"src", dst:"dst", hasPending:true).save(flush:true)
			new Fmessage(src:"src", dst:"dst", hasPending:true).save(flush:true)
		when:
			go "message/pending"
		then:
			at PageMessagePending
			!$("#retry").displayed
		when:
			messagesSelect[0].click()
		then:
			waitFor { $("#multiple-messages").displayed }
			!$("#retry-failed").displayed
	}

	def "should be able to retry a failed message"() {
		when:
			go "message/pending"
		then:
			at PageMessagePending
		when:
			$("a", text:"dst1").click()
		then:
			waitFor { $("#retry").displayed }
		when:
			$("#retry").click()
		then:	
			waitFor { $(".flash").text()?.contains("dst1") }
	}

	def "should be able to retry all failed messages"() {
		setup:
			new Fmessage(src: "src1", dst:"dst2", hasFailed:true, starred: true).save(flush: true, failOnError:true)
		when:
			go "message/pending"
		then:
			at PageMessagePending
		when:
			$("a", text:"dst1").click()
		then:
			waitFor { $("#retry").displayed }
		when:
			messagesSelect[0].click()
		then:
			waitFor { $("#retry-failed").displayed }
		when:
			$("#retry-failed").click()
		then:
			waitFor{ $(".flash").text()?.contains("dst2, dst1") }
	}
}
